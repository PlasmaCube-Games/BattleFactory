#!/usr/bin/env python3
"""
Convert a community Battle Factory sheet CSV into JSON configs.

Usage: python sheet_to_json.py <csv_path> <configs_dir> <pool_name> <preset_prefix> [level]
  <pool_name>     — key for the rental pool (e.g. "easy_pool", "hard_pool")
  <preset_prefix> — prefix added to preset keys (e.g. "easy_", "hard_")
  [level]         — level to set for every preset (defaults to 50)

Produces/updates:
- pokemon_presets.json — adds/overwrites every preset keyed `<prefix><id>_<species>[_<form>]`
- rental_pools.json    — replaces the pool `<pool_name>` (type "set") with all those presets
"""

from __future__ import annotations

import csv
import json
import re
import sys
import pathlib

# --- species + form normalisation -------------------------------------------

FORM_RE = re.compile(r"\s*\((Galarian|Alolan|Hisuian|Kanto|Paldean|Galar|Hisui|Alola|Paldea)\)\s*", re.I)

FORM_TOKEN_TO_FEATURE = {
    "galarian": "galarian",
    "galar": "galarian",
    "alolan": "alolan",
    "alola": "alolan",
    "hisuian": "hisuian",
    "hisui": "hisuian",
    "paldean": "paldean",
    "paldea": "paldean",
    "kanto": None,
}

SPECIES_ALIASES = {
    "mrmime": "mrmime",
    "mr.mime": "mrmime",
    "mr-mime": "mrmime",
    # Common misspellings in community sheets → correct Cobblemon species IDs
    "vivilon": "vivillon",
    "victreebell": "victreebel",
    "leidan": "ledian",
    "farfetchd": "farfetchd",
    "nidoranf": "nidoranf",
    "nidoranm": "nidoranm",
    "jangmoo": "jangmoo",
    "hakamoo": "hakamoo",
    "kommoo": "kommoo",
    "typenull": "typenull",
    "hooh": "hooh",
    "porygonz": "porygonz",
    "porygon2": "porygon2",
    "mimejr": "mimejr",
    "flabebe": "flabebe",
    "sirfetchd": "sirfetchd",
    "mrrime": "mrrime",
    "tapukoko": "tapukoko",
    "tapulele": "tapulele",
    "tapubulu": "tapubulu",
    "tapufini": "tapufini",
}


def normalize_species(raw: str) -> tuple[str, str]:
    """Return (species_id, species_features)."""
    name = raw.strip()
    feature = ""
    # Regional form in parentheses
    m = FORM_RE.search(name)
    if m:
        token = m.group(1).lower()
        region = FORM_TOKEN_TO_FEATURE.get(token)
        if region:
            feature = f"form={region}"
        name = FORM_RE.sub("", name)
    # Try to parse anything like "Rapidash(Galarian)" without a space
    m2 = re.match(r"(.+?)\(([A-Za-z]+)\)\s*$", name)
    if m2:
        token = m2.group(2).lower()
        region = FORM_TOKEN_TO_FEATURE.get(token)
        if region:
            feature = f"form={region}"
        name = m2.group(1)
    species = re.sub(r"[^a-z0-9]", "", name.lower())
    species = SPECIES_ALIASES.get(species, species)
    return species, feature


def normalize_move(raw: str) -> str:
    s = raw.strip().lower()
    # strip common punctuation / whitespace / hyphens
    s = re.sub(r"[^a-z0-9]", "", s)
    return s


def normalize_ability(raw: str) -> str:
    return re.sub(r"[^a-z0-9]", "", raw.strip().lower())


def ev_spread(row: dict) -> str:
    """Pick a 252/252/4 spread based on the two highest *real* stats in the sheet.

    Stats order in game: HP, ATK, DEF, SPA, SPD, SPE.
    """
    stat_names = ["HP", "Atk", "Def", "SpA", "SpD", "Spe"]
    try:
        vals = [(i, int(row[n])) for i, n in enumerate(stat_names)]
    except (KeyError, ValueError):
        return "0,252,0,0,4,252"
    vals.sort(key=lambda t: -t[1])
    top = {vals[0][0], vals[1][0]}
    third = vals[2][0]
    out = [0] * 6
    for idx in top:
        out[idx] = 252
    out[third] = 4
    return ",".join(str(v) for v in out)


def build_preset(row: dict, prefix: str, level: int = 50) -> tuple[str, dict]:
    raw_name = row["Pokémon"].strip()
    species, feature = normalize_species(raw_name)
    ability = normalize_ability(row.get("Ability", ""))
    moves = []
    for k in ("Move 1", "Move 2", "Move 3", "Move 4"):
        m = normalize_move(row.get(k, ""))
        if m:
            moves.append(m)
    key = f"{prefix}{row['ID'].strip().zfill(4)}_{species}"
    if feature:
        key = f"{key}_{feature.split('=')[1]}"
    preset = {
        "species": species,
        "level_range": {"min": level, "max": level},
        "species_features": feature,
        "possible_genders": [{"weight": 1, "gender": "male"}],
        "possible_abilities": (
            [{"weight": 1, "ability": ability}] if ability else []
        ),
        "possible_natures": [{"weight": 1, "nature": "hardy"}],
        "known_moves": moves,
        "possible_held_items": [],
        "ivs": "31,31,31,31,31,31",
        "evs": ev_spread(row),
    }
    return key, preset


def main() -> None:
    if len(sys.argv) not in (5, 6):
        print("usage: sheet_to_json.py <csv> <configs_dir> <pool_name> <preset_prefix> [level]", file=sys.stderr)
        sys.exit(2)
    csv_path = pathlib.Path(sys.argv[1])
    cfg_dir = pathlib.Path(sys.argv[2])
    pool_name = sys.argv[3]
    preset_prefix = sys.argv[4]
    level = int(sys.argv[5]) if len(sys.argv) == 6 else 50
    cfg_dir.mkdir(parents=True, exist_ok=True)

    presets: dict[str, dict] = {}
    pool_entries = []

    with csv_path.open(encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if not row.get("ID") or not row["ID"].strip().isdigit():
                continue
            if not row.get("Pokémon"):
                continue
            key, preset = build_preset(row, preset_prefix, level)
            presets[key] = preset
            pool_entries.append({"weight": 1, "pokemon_preset": key})

    presets_path = cfg_dir / "pokemon_presets.json"
    pools_path = cfg_dir / "rental_pools.json"

    if presets_path.exists():
        existing = json.loads(presets_path.read_text(encoding="utf-8"))
    else:
        existing = {}
    # drop any stale presets with this prefix so re-runs replace cleanly
    existing = {k: v for k, v in existing.items() if not k.startswith(preset_prefix)}
    existing.update(presets)
    presets_path.write_text(
        json.dumps(existing, indent=2, ensure_ascii=False), encoding="utf-8"
    )

    if pools_path.exists():
        pools = json.loads(pools_path.read_text(encoding="utf-8"))
    else:
        pools = {}
    pools[pool_name] = {"type": "set", "pokemon": pool_entries}
    pools_path.write_text(
        json.dumps(pools, indent=2, ensure_ascii=False), encoding="utf-8"
    )

    print(f"wrote {len(presets)} presets (prefix '{preset_prefix}') -> {presets_path}")
    print(f"wrote {pool_name} with {len(pool_entries)} entries -> {pools_path}")


if __name__ == "__main__":
    main()
