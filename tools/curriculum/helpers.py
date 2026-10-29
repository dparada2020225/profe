"""Helpers para escribir el contenido del curso de forma compacta.

Formato del cuerpo de las secciones (lo interpreta la app):
  - Párrafos separados por línea en blanco.
  - Líneas que empiezan con "- " son viñetas.
  - **negrita** para resaltar.
  - Líneas que empiezan con "> " son ejemplos (se pueden escuchar).
"""
import textwrap

def U(id, title, subtitle, goal, sections, examples, vocab, exercises):
    return {
        "id": id,
        "title": title,
        "subtitle": subtitle,
        "goal": goal,
        "sections": [{"heading": h, "body": textwrap.dedent(b).strip()} for h, b in sections],
        "examples": [{"en": en, "es": es} for en, es in examples],
        "vocab": [{"en": en, "es": es, "ex": ex} for en, es, ex in vocab],
        "exercises": exercises,
    }

def C(prompt, options, answer, explain=""):
    """Opción múltiple. `answer` es el índice correcto."""
    assert 0 <= answer < len(options), prompt
    return {"type": "choice", "prompt": prompt, "options": options, "answer": answer, "explain": explain}

def F(prompt, answers, explain="", hint=""):
    """Completar: el hueco se marca con ___ ; respuestas válidas separadas por |."""
    assert "___" in prompt, prompt
    return {"type": "fill", "prompt": prompt, "answers": answers.split("|"), "explain": explain, "hint": hint}

def T(es, answers, explain=""):
    """Traducir del español al inglés. Respuestas válidas separadas por |."""
    return {"type": "translate", "prompt": es, "answers": answers.split("|"), "explain": explain}

def O(en, es=""):
    """Ordenar las palabras de una oración."""
    return {"type": "order", "prompt": es, "answers": [en]}
