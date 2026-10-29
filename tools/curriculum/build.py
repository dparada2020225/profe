"""Genera app/src/main/assets/curriculum/*.json a partir de los módulos de cada nivel.

Uso:  python3 build.py
Además de los ejercicios escritos a mano, agrega ejercicios automáticos a partir de los
ejemplos (ordenar, dictado, pronunciación) y del vocabulario (significado).
"""
import json, os, random, re, sys

sys.path.insert(0, os.path.dirname(__file__))
import a1, a2, b1, b2, c1, c2
from placement import PLACEMENT

OUT = os.path.join(os.path.dirname(__file__), "..", "..", "app", "src", "main", "assets", "curriculum")
LEVELS = [a1.LEVEL, a2.LEVEL, b1.LEVEL, b2.LEVEL, c1.LEVEL, c2.LEVEL]


def words(s):
    return re.findall(r"[A-Za-z0-9']+", s)


def auto_exercises(unit, rnd):
    ex = []
    examples = unit["examples"]
    # Ordenar palabras: 2 ejemplos de 4 a 10 palabras
    candidates = [e for e in examples if 4 <= len(words(e["en"])) <= 10]
    for e in candidates[:2]:
        ex.append({"type": "order", "prompt": e["es"], "answers": [e["en"]]})
    # Dictado: 2 ejemplos
    for e in examples[2:4]:
        ex.append({"type": "listen", "prompt": e["es"], "answers": [e["en"]]})
    # Pronunciación: 1 ejemplo
    for e in examples[4:5]:
        ex.append({"type": "speak", "prompt": e["es"], "answers": [e["en"]]})
    # Vocabulario: 2 preguntas de significado con distractores de la misma unidad
    vocab = unit["vocab"]
    if len(vocab) >= 4:
        picks = rnd.sample(vocab, 2)
        for v in picks:
            others = [o["es"] for o in vocab if o is not v]
            opts = rnd.sample(others, 2) + [v["es"]]
            rnd.shuffle(opts)
            ex.append({"type": "choice", "prompt": f"¿Qué significa «{v['en']}»?", "options": opts,
                       "answer": opts.index(v["es"]), "explain": v["ex"]})
    return ex


def validate(level):
    ids = set()
    for u in level["units"]:
        assert u["id"] not in ids, u["id"]
        ids.add(u["id"])
        assert u["sections"] and u["examples"] and u["vocab"] and u["exercises"], u["id"]
        for e in u["exercises"]:
            if e["type"] == "choice":
                assert len(set(e["options"])) == len(e["options"]), (u["id"], e["prompt"])
            if e["type"] == "fill":
                assert e["prompt"].count("___") == 1, (u["id"], e["prompt"])
                assert all(a.strip() for a in e["answers"]), (u["id"], e["prompt"])


def main():
    rnd = random.Random(42)
    os.makedirs(OUT, exist_ok=True)
    total_units = total_ex = total_vocab = 0
    for level in LEVELS:
        validate(level)
        for u in level["units"]:
            u["exercises"] = u["exercises"] + auto_exercises(u, rnd)
            total_units += 1
            total_ex += len(u["exercises"])
            total_vocab += len(u["vocab"])
    curriculum = {"version": 1, "levels": LEVELS}
    with open(os.path.join(OUT, "curriculum.json"), "w", encoding="utf-8") as f:
        json.dump(curriculum, f, ensure_ascii=False, separators=(",", ":"))
    with open(os.path.join(OUT, "placement.json"), "w", encoding="utf-8") as f:
        json.dump({"version": 1, "levels": [{"id": k, "questions": v} for k, v in PLACEMENT.items()]},
                  f, ensure_ascii=False, separators=(",", ":"))
    print(f"{len(LEVELS)} niveles, {total_units} unidades, {total_ex} ejercicios, {total_vocab} palabras")


if __name__ == "__main__":
    main()
