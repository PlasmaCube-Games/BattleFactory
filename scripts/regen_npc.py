#!/usr/bin/env python3
"""
Rebuild the NPC pool in bf_npc_easy.json from pokemon_presets.json.

Every preset generated from the community sheet (keyed `lowtier_...`) becomes
a pool entry at level 50 / 31 IVs. Preserves the NPC file's top-level metadata
(names, AI, behaviours, etc.) and only replaces party.pool.

Usage: python regen_npc.py <presets_json> <npc_json> <level> <preset_prefix>
"""
from __future__ import annotations
import json
import sys
import pathlib


DEFAULT_LEVEL = 50
EV_KEYS = ["hp_ev", "attack_ev", "defence_ev", "special_attack_ev", "special_defence_ev", "speed_ev"]


def props_for(preset: dict, level: int = DEFAULT_LEVEL) -> str:
    parts = [preset["species"]]
    feature = preset.get("species_features", "")
    if feature.startswith("form="):
        parts.append(feature.split("=", 1)[1])
    parts += [
        "uncatchable=true",
        f"level={level}",
        "hp_iv=31", "attack_iv=31", "defence_iv=31",
        "special_attack_iv=31", "special_defence_iv=31", "speed_iv=31",
    ]
    # Match the player-side EV spread so NPC has the same stats
    evs = str(preset.get("evs", "0,0,0,0,0,0")).split(",")
    if len(evs) == 6:
        for k, v in zip(EV_KEYS, evs):
            parts.append(f"{k}={v.strip()}")
    abilities = preset.get("possible_abilities", [])
    if abilities:
        parts.append(f"ability={abilities[0]['ability']}")
    moves = preset.get("known_moves", [])
    if moves:
        parts.append("moves=" + ":".join(moves[:4]))
    return " ".join(parts)


def main() -> None:
    if len(sys.argv) != 5:
        print("usage: regen_npc.py <presets_json> <npc_json> <level> <preset_prefix>", file=sys.stderr)
        sys.exit(2)
    presets_path = pathlib.Path(sys.argv[1])
    npc_path = pathlib.Path(sys.argv[2])
    level = int(sys.argv[3])
    prefix = sys.argv[4]

    presets = json.loads(presets_path.read_text(encoding="utf-8"))
    npc = json.loads(npc_path.read_text(encoding="utf-8"))

    pool = []
    for key, preset in presets.items():
        if not key.startswith(prefix):
            continue
        pool.append({
            "pokemon": props_for(preset, level),
            "weight": 10,
            "selectableTimes": 1,
            "levelVariation": "0",
        })

    npc.setdefault("party", {})
    npc["party"]["type"] = "pool"
    npc["party"]["minPokemon"] = 3
    npc["party"]["maxPokemon"] = 3
    npc["party"]["isStatic"] = False
    npc["party"]["pool"] = pool

    npc_path.write_text(
        json.dumps(npc, indent=2, ensure_ascii=False), encoding="utf-8"
    )
    print(f"rebuilt {npc_path} with {len(pool)} pool entries")


if __name__ == "__main__":
    main()