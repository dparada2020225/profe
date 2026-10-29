package com.denil.profe.data.seed

/** Mazo de ejemplo: 50 palabras de inglés de nivel B1-B2. */
object SampleDeck {
    const val NAME = "Inglés B1-B2 · Vocabulario esencial"
    const val DESCRIPTION = "50 palabras y expresiones frecuentes de nivel intermedio, con ejemplo de uso."

    data class Word(val front: String, val back: String, val example: String, val tags: String)

    val words: List<Word> = listOf(
        Word("achieve", "lograr, alcanzar", "She worked hard to achieve her goals.", "verbo"),
        Word("afford", "permitirse (económicamente)", "I can't afford a new car right now.", "verbo,dinero"),
        Word("although", "aunque", "Although it was raining, we went for a walk.", "conector"),
        Word("appointment", "cita (médica, de trabajo)", "I have a dentist appointment at 3 p.m.", "sustantivo"),
        Word("argue", "discutir, argumentar", "They always argue about politics.", "verbo"),
        Word("available", "disponible", "Is this seat available?", "adjetivo"),
        Word("avoid", "evitar", "Try to avoid eating too much sugar.", "verbo"),
        Word("aware", "consciente (de algo)", "Are you aware of the risks?", "adjetivo"),
        Word("behave", "comportarse", "The children behaved very well at the party.", "verbo"),
        Word("borrow", "pedir prestado", "Can I borrow your pen for a second?", "verbo"),
        Word("challenge", "desafío, reto", "Learning a language is a real challenge.", "sustantivo"),
        Word("confident", "seguro de sí mismo", "She felt confident before the interview.", "adjetivo"),
        Word("convenient", "conveniente, práctico", "Is 10 a.m. convenient for you?", "adjetivo"),
        Word("deadline", "fecha límite", "The deadline for the project is Friday.", "sustantivo,trabajo"),
        Word("deliver", "entregar", "The package will be delivered tomorrow.", "verbo"),
        Word("disappointed", "decepcionado", "I was disappointed with the movie.", "adjetivo,emociones"),
        Word("embarrassed", "avergonzado", "He was embarrassed when he forgot her name.", "adjetivo,emociones"),
        Word("encourage", "animar, alentar", "My teacher encouraged me to apply.", "verbo"),
        Word("environment", "medio ambiente; entorno", "We must protect the environment.", "sustantivo"),
        Word("eventually", "finalmente, al final", "Eventually, they found the right answer.", "adverbio"),
        Word("figure out", "descubrir, resolver", "I can't figure out how this works.", "phrasal verb"),
        Word("get along with", "llevarse bien con", "I get along with my coworkers.", "phrasal verb"),
        Word("give up", "rendirse, dejar de hacer algo", "Don't give up! You're almost there.", "phrasal verb"),
        Word("however", "sin embargo", "It was expensive; however, it was worth it.", "conector"),
        Word("improve", "mejorar", "Your English has improved a lot.", "verbo"),
        Word("in charge of", "a cargo de, encargado de", "Who is in charge of this project?", "expresión,trabajo"),
        Word("increase", "aumentar; aumento", "Prices increased by 5% last year.", "verbo,sustantivo"),
        Word("involve", "implicar, involucrar", "The job involves a lot of travel.", "verbo"),
        Word("issue", "asunto, problema", "We need to discuss this issue.", "sustantivo"),
        Word("likely", "probable", "It's likely to rain this afternoon.", "adjetivo"),
        Word("look forward to", "tener ganas de, esperar con ilusión", "I'm looking forward to the weekend.", "phrasal verb"),
        Word("manage", "lograr; gestionar", "She managed to finish on time.", "verbo"),
        Word("mention", "mencionar", "He didn't mention the meeting.", "verbo"),
        Word("nevertheless", "no obstante", "It was hard; nevertheless, we succeeded.", "conector"),
        Word("opportunity", "oportunidad", "This job is a great opportunity.", "sustantivo"),
        Word("overwhelmed", "abrumado", "I feel overwhelmed with work.", "adjetivo,emociones"),
        Word("point out", "señalar, indicar", "She pointed out a mistake in the report.", "phrasal verb"),
        Word("purpose", "propósito", "What is the purpose of this meeting?", "sustantivo"),
        Word("put off", "posponer", "Don't put off your homework until Sunday.", "phrasal verb"),
        Word("rely on", "depender de, confiar en", "You can rely on me.", "phrasal verb"),
        Word("require", "requerir, exigir", "This position requires experience.", "verbo"),
        Word("run out of", "quedarse sin", "We've run out of milk.", "phrasal verb"),
        Word("schedule", "horario, programa; programar", "Let me check my schedule.", "sustantivo,verbo"),
        Word("skill", "habilidad", "Communication is an important skill.", "sustantivo"),
        Word("suitable", "adecuado, apropiado", "This movie isn't suitable for children.", "adjetivo"),
        Word("take advantage of", "aprovechar", "Take advantage of the discount today.", "expresión"),
        Word("therefore", "por lo tanto", "It was late; therefore, we went home.", "conector"),
        Word("turn down", "rechazar", "He turned down the job offer.", "phrasal verb"),
        Word("upset", "molesto, disgustado", "She was upset about the news.", "adjetivo,emociones"),
        Word("whereas", "mientras que", "I like tea, whereas my brother prefers coffee.", "conector")
    )
}
