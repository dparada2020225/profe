from helpers import U, C, F, T, O

LEVEL = {
    "id": "C2",
    "title": "C2 · Maestría",
    "description": "Manejas matices finos, ironía y registro como un hablante culto. El tutor te ayuda a pulir todo lo demás.",
    "units": [
        U("c2-01", "I was wondering…", "Tense for distance and nuance",
          "Use tenses not for time, but for politeness, distance and attitude.",
          [("Distancing with past forms", """
            Past and continuous forms make requests and statements **less direct**:

            - *Do you want…?* → ***Did you want** anything else?* (waiter, polite)
            - *I hope you can help* → ***I was hoping** you could help.*
            - *I wonder if…* → ***I was wondering** if…*
            - ***I wanted to ask** you something.* (but I'm asking now)
            """),
           ("Would for habits and 'was going to'", """
            - **would** for repeated past actions (storytelling, nostalgic): *Every summer, we **would** go to the lake.* (not with states: *I would have a dog* ❌)
            - **was / were going to** → a plan that didn't happen: *I **was going to** call you, but I fell asleep.*
            - **will** for typical behavior / annoyance: *She **will** keep talking during movies!*
            - **be always -ing** for irritating habits: *He's **always losing** his keys.*
            """)],
          [("I was wondering if you could review my code.", "Me preguntaba si podrías revisar mi código."),
           ("Did you want anything else?", "¿Deseaba algo más?"),
           ("I was hoping we could talk.", "Esperaba que pudiéramos hablar."),
           ("Every Sunday, my grandfather would tell us stories.", "Cada domingo, mi abuelo nos contaba historias."),
           ("I was going to call you, but I fell asleep.", "Te iba a llamar, pero me quedé dormido."),
           ("He's always interrupting me!", "¡Siempre me está interrumpiendo!"),
           ("Boys will be boys.", "Los niños son así."),
           ("We were going to travel, but it got canceled.", "Íbamos a viajar, pero se canceló.")],
          [("nuance", "matiz", "There's a subtle nuance here."),
           ("tentative", "tentativo, prudente", "A tentative request."),
           ("nostalgic", "nostálgico", "It makes me nostalgic."),
           ("habitual", "habitual", "Habitual actions in the past."),
           ("fall asleep", "quedarse dormido", "I fell asleep on the sofa."),
           ("subtle", "sutil", "A subtle difference."),
           ("politeness", "cortesía", "It's a matter of politeness."),
           ("annoyance", "molestia", "She showed her annoyance."),
           ("wonder", "preguntarse", "I wonder why."),
           ("intend", "tener intención de", "I intended to call."),
           ("storytelling", "narración", "Would is common in storytelling."),
           ("attitude", "actitud", "Tense can show attitude.")],
          [C("Most polite:", ["I want to ask you something.", "I was wondering if I could ask you something.", "Answer this."], 1),
           C("When I was a kid, my dad ___ take us fishing every weekend.", ["would", "will", "was going to"], 0),
           C("'Would' cannot be used for past states. Which is wrong?", ["We would play outside.", "I would have long hair.", "She would read to us."], 1),
           C("Plan that didn't happen:", ["I was going to call you.", "I'm going to call you.", "I would call you."], 0),
           C("Irritating habit:", ["He always loses his keys.", "He's always losing his keys!", "He lost his keys."], 1),
           F("I was ___ you could help me with something. (hoping)", "hoping"),
           F("We ___ going to buy it, but it was sold out.", "were"),
           T("Te iba a llamar, pero me quedé dormido.", "I was going to call you, but I fell asleep|I was going to call you but I fell asleep"),
           T("Me preguntaba si podrías ayudarme.", "I was wondering if you could help me|I was wondering whether you could help me")]),

        U("c2-02", "Be that as it may", "Advanced discourse markers and cohesion",
          "Connect complex ideas with precision and sophistication.",
          [("Concession and contrast", """
            - **Be that as it may** = even so → *The plan is risky. **Be that as it may**, it's our best option.*
            - **Granted, … (but)** → ***Granted**, it's expensive, but it'll last for years.*
            - **Admittedly** → ***Admittedly**, I didn't read the whole report.*
            - **Notwithstanding** (formal) = despite
            - **Even so / All the same / Nonetheless**
            """),
           ("Structuring an argument", """
            - Adding: **What is more**, **Not to mention**, **Coupled with**
            - Clarifying: **That is to say**, **In other words**, **To put it another way**
            - Focusing: **As far as … is concerned**, **With regard to**, **In terms of**
            - Concluding: **All things considered**, **On balance**, **In the final analysis**
            - Changing topic: **Incidentally**, **By the way**, **Speaking of which**
            """)],
          [("Be that as it may, we have no other option.", "Sea como sea, no tenemos otra opción."),
           ("Granted, it's expensive, but it's worth it.", "Es cierto que es caro, pero vale la pena."),
           ("Admittedly, I made a mistake.", "Hay que admitir que cometí un error."),
           ("On balance, I think it was the right decision.", "En general, creo que fue la decisión correcta."),
           ("As far as I'm concerned, the matter is closed.", "Por lo que a mí respecta, el asunto está cerrado."),
           ("In terms of cost, the second option is better.", "En cuanto al costo, la segunda opción es mejor."),
           ("All things considered, it went well.", "Considerándolo todo, salió bien."),
           ("Incidentally, have you heard from Ana?", "Por cierto, ¿has sabido algo de Ana?")],
          [("be that as it may", "sea como sea", "Be that as it may, we must continue."),
           ("granted", "es cierto que / concedido", "Granted, it's not perfect."),
           ("admittedly", "hay que reconocer que", "Admittedly, it was my fault."),
           ("notwithstanding", "no obstante / a pesar de", "Notwithstanding the risks…"),
           ("on balance", "en general / sopesándolo todo", "On balance, it's positive."),
           ("all things considered", "considerándolo todo", "All things considered, yes."),
           ("as far as … is concerned", "en lo que respecta a", "As far as I'm concerned…"),
           ("in terms of", "en términos de", "In terms of speed, it's great."),
           ("incidentally", "por cierto", "Incidentally, I saw your brother."),
           ("that is to say", "es decir", "In May, that is to say, next month."),
           ("nonetheless", "no obstante", "It was hard; nonetheless, we did it."),
           ("cohesion", "cohesión", "Linking words create cohesion.")],
          [C("It's risky. ___, it's our only chance.", ["Be that as it may", "In terms of", "Incidentally"], 0),
           C("___, the price is high, but the quality is excellent.", ["Granted", "Therefore", "Incidentally"], 0),
           C("Conclude after weighing pros and cons:", ["On balance", "By the way", "Not to mention"], 0),
           C("___ quality is concerned, we're the best.", ["As far as", "As long as", "As well as"], 0),
           C("Change of topic:", ["Consequently", "Incidentally", "Nonetheless"], 1),
           F("In ___ of performance, the new phone is faster.", "terms"),
           F("All things ___, the trip was a success.", "considered"),
           F("The task was difficult; ___, we finished on time.", "nonetheless|nevertheless|however"),
           T("Por lo que a mí respecta, el asunto está cerrado.", "As far as I'm concerned, the matter is closed|As far as I am concerned, the matter is closed")]),

        U("c2-03", "Mind your register", "Register shifting: formal, neutral, informal",
          "Rewrite the same message for different audiences.",
          [("Same idea, three registers", """
            - Informal: *Sorry, can't make it tonight — something came up.*
            - Neutral: *I'm sorry, but I won't be able to come tonight. Something has come up.*
            - Formal: *Please accept my apologies; I regret that I will be unable to attend this evening due to unforeseen circumstances.*
            """),
           ("Markers of register", """
            **Informal**: contractions, phrasal verbs, ellipsis (*Sounds good!*), slang (*gonna, kinda*), direct questions.
            **Formal**: full forms, Latin-based vocabulary (*commence, endeavour*), passive voice, impersonal structures (*It is advisable to…*), indirect questions (*Could you tell me whether…*).

            Common pitfall for advanced learners: being **too formal** in casual contexts (sounds cold or sarcastic) — e.g., Slack messages to a colleague don't need *I hereby inform you*.
            """)],
          [("Sorry, can't make it tonight.", "Perdón, no puedo ir esta noche."),
           ("I regret that I will be unable to attend.", "Lamento no poder asistir."),
           ("Could you let me know whether the position is still open?", "¿Podría informarme si el puesto sigue disponible?"),
           ("It is advisable to book in advance.", "Es aconsejable reservar con anticipación."),
           ("Sounds good, see you then!", "¡Me parece bien, nos vemos!"),
           ("Due to unforeseen circumstances, the event has been postponed.", "Debido a circunstancias imprevistas, el evento ha sido pospuesto."),
           ("Hey, just checking in — any updates?", "Hola, solo para saber si hay novedades."),
           ("We would be grateful if you could reply at your earliest convenience.", "Le agradeceríamos que respondiera a la mayor brevedad posible.")],
          [("register", "registro (formalidad)", "Adapt your register to the audience."),
           ("unforeseen", "imprevisto", "Unforeseen circumstances."),
           ("at your earliest convenience", "a la mayor brevedad", "Please reply at your earliest convenience."),
           ("advisable", "aconsejable", "It is advisable to arrive early."),
           ("endeavour / endeavor", "esforzarse / empeño", "We endeavor to reply within 24 hours."),
           ("commence", "comenzar (formal)", "The ceremony will commence at noon."),
           ("grateful", "agradecido", "I'd be grateful for your help."),
           ("check in", "reportarse / dar seguimiento", "Just checking in."),
           ("come up", "surgir (imprevisto)", "Something came up."),
           ("make it", "lograr ir / llegar", "Can you make it tomorrow?"),
           ("regards", "saludos (cierre)", "Kind regards, Denil"),
           ("hereby", "por la presente", "I hereby resign.")],
          [C("Most formal:", ["Can't come, sorry!", "I'm afraid I can't come.", "I regret that I will be unable to attend."], 2),
           C("Most natural on Slack to a coworker:", ["I hereby request the file.", "Hey, could you send me the file when you get a sec?", "Send file."], 1),
           C("Formal equivalent of 'start':", ["kick off", "commence", "get going"], 1),
           C("Informal equivalent of 'I am unable to attend':", ["I can't make it.", "I cannot attend.", "Attendance is impossible."], 0),
           C("Formal indirect question:", ["Is the job still open?", "Could you tell me whether the position is still available?", "Job still open?"], 1),
           F("Please reply at your earliest ___.", "convenience"),
           F("Something came ___, so I can't go.", "up"),
           F("It is ___ to book in advance. (aconsejable)", "advisable|recommended"),
           T("Lamento no poder asistir.", "I regret that I will be unable to attend|I regret that I won't be able to attend|I'm sorry I can't attend|I regret being unable to attend")]),

        U("c2-04", "Rarely seen, much admired", "Fronting and emphatic structures",
          "Move elements to the front and use emphatic forms for style and impact.",
          [("Fronting", """
            Placing an element at the start for emphasis or contrast:

            - *I can't stand his attitude* → ***His attitude** I can't stand.*
            - ***Strange** as it may seem, I enjoyed it.* (= Although it may seem strange)
            - ***Try as I might**, I couldn't open it.* (= However hard I tried)
            - ***Much as** I admire her, I disagree.* (= Although I admire her a lot)
            - ***Down came** the rain.* / ***Here comes** the bus.* (locative inversion)
            """),
           ("Emphatic do and intensifiers", """
            - Emphatic **do/does/did**: *I **do** understand your concerns. She **did** call — you just missed it.*
            - **the very**: *That's **the very** thing I was looking for.*
            - **whatsoever / at all**: *I have no idea **whatsoever**.*
            - **on earth / ever** in questions: *What **on earth** happened? Who **ever** told you that?*
            """)],
          [("Strange as it may seem, I loved it.", "Por extraño que parezca, me encantó."),
           ("Try as I might, I couldn't fix it.", "Por más que lo intenté, no pude arreglarlo."),
           ("Much as I like him, I don't trust him.", "Por mucho que me caiga bien, no confío en él."),
           ("I do appreciate your help.", "De verdad agradezco tu ayuda."),
           ("What on earth are you doing?", "¿Qué demonios estás haciendo?"),
           ("I have no idea whatsoever.", "No tengo ni la menor idea."),
           ("That's the very reason I left.", "Esa es precisamente la razón por la que me fui."),
           ("Here comes the bus.", "Aquí viene el bus.")],
          [("strange as it may seem", "por extraño que parezca", "Strange as it may seem, it worked."),
           ("try as I might", "por más que lo intenté", "Try as I might, I failed."),
           ("much as", "por mucho que", "Much as I'd like to, I can't."),
           ("whatsoever", "en absoluto", "No doubt whatsoever."),
           ("the very", "precisamente el / mismo", "At that very moment…"),
           ("on earth", "demonios (énfasis)", "Where on earth is it?"),
           ("admire", "admirar", "I admire her courage."),
           ("trust", "confiar", "I don't trust him."),
           ("appreciate", "agradecer / valorar", "I really appreciate it."),
           ("concern", "preocupación", "I understand your concerns."),
           ("impact", "impacto", "Fronting adds impact."),
           ("contrast", "contraste", "In contrast, …")],
          [C("___ as it may seem, I've never seen the sea.", ["Strange", "Strangely", "Strangeness"], 0),
           C("___ as I might, I couldn't remember her name.", ["Try", "Tried", "Trying"], 0),
           C("Emphatic: 'I really did call you.'", ["I called you!", "I did call you!", "I have called you!"], 1),
           C("I have no money ___.", ["whatsoever", "whatever", "anyhow"], 0),
           C("What ___ earth is that noise?", ["in", "on", "at"], 1),
           F("___ as I respect him, I can't support this decision.", "Much"),
           F("That's the ___ thing I was going to say!", "very"),
           F("I ___ understand your point — I just disagree. (énfasis)", "do"),
           T("Por extraño que parezca, me encantó.", "Strange as it may seem, I loved it|Strange as it may seem, I loved it.|Strange though it may seem, I loved it")]),

        U("c2-05", "Slim, skinny or slender?", "Connotation and near-synonyms",
          "Choose words with the right shade of meaning.",
          [("Positive, neutral, negative", """
            Words can mean almost the same thing but carry different **connotations**:

            - **slender / slim** (positive) · **thin** (neutral) · **skinny / scrawny** (negative)
            - **confident** (+) · **self-assured** (+/neutral) · **arrogant / cocky** (−)
            - **thrifty / economical** (+) · **careful with money** (neutral) · **stingy / cheap** (−)
            - **curious** (+) · **inquisitive** (neutral) · **nosy** (−)
            - **determined** (+) · **stubborn** (−) · **pig-headed** (very −)
            """),
           ("Precise verbs", """
            Advanced speakers prefer precise verbs over *say / look / walk + adverb*:

            - say → **whisper, mutter, claim, insist, point out, hint**
            - look → **glance** (quick), **stare** (long), **glare** (angry), **peer** (difficult to see)
            - walk → **stroll** (relaxed), **stride** (confident), **wander** (no direction), **limp** (injured)
            - increase → **soar** (rapidly), **creep up** (slowly), **double**
            """)],
          [("She's slim and athletic.", "Ella es delgada y atlética."),
           ("He's a bit stubborn.", "Él es un poco terco."),
           ("Don't be so nosy!", "¡No seas tan metiche!"),
           ("He glanced at his watch.", "Echó un vistazo a su reloj."),
           ("She glared at me.", "Me lanzó una mirada furiosa."),
           ("We strolled along the beach.", "Paseamos por la playa."),
           ("Prices have soared this year.", "Los precios se han disparado este año."),
           ("He muttered something and left.", "Murmuró algo y se fue.")],
          [("connotation", "connotación", "The word has a negative connotation."),
           ("stubborn", "terco", "Don't be so stubborn."),
           ("nosy", "metiche", "My neighbor is nosy."),
           ("stingy", "tacaño", "He's too stingy to buy a gift."),
           ("thrifty", "ahorrativo", "My grandma is very thrifty."),
           ("glance", "vistazo / echar un vistazo", "She glanced at the screen."),
           ("stare", "mirar fijamente", "Don't stare at people."),
           ("stroll", "pasear", "Let's stroll around the park."),
           ("wander", "deambular", "We wandered through the market."),
           ("soar", "dispararse / elevarse", "Temperatures soared."),
           ("mutter", "murmurar", "He muttered an apology."),
           ("hint", "insinuar / pista", "She hinted that she was leaving.")],
          [C("Positive word for 'careful with money':", ["stingy", "thrifty", "cheap"], 1),
           C("Negative word for 'determined':", ["persistent", "stubborn", "resolute"], 1),
           C("A quick look:", ["stare", "glance", "glare"], 1),
           C("An angry look:", ["glare", "peer", "glance"], 0),
           C("A relaxed walk:", ["stride", "limp", "stroll"], 2),
           C("Prices increased very rapidly. Prices ___.", ["crept up", "soared", "wandered"], 1),
           F("She ___ at the tiny text, trying to read it. (mirar con dificultad)", "peered"),
           F("Don't be so ___ — it's none of your business! (metiche)", "nosy|nosey"),
           T("Paseamos por la playa.", "We strolled along the beach|We walked along the beach|We took a stroll along the beach")]),

        U("c2-06", "Food for thought", "Figurative and idiomatic language",
          "Understand and use metaphor, understatement and irony.",
          [("Metaphor in everyday English", """
            Many abstract ideas are expressed through concrete images:

            - Ideas are food: *food for thought*, *half-baked idea*, *digest the information*, *hard to swallow*
            - Time is money: *spend / waste / save / invest time*, *running out of time*
            - Argument is war: *defend a position*, *attack a point*, *shoot down an idea*
            - Understanding is seeing: *I see what you mean*, *shed light on*, *a clear explanation*
            """),
           ("Understatement and irony", """
            British and American speakers often use **understatement**: *It's **not exactly** cheap* (= very expensive). *I'm **not the biggest fan** of…* (= I dislike).

            Irony: saying the opposite with a specific tone: *Oh, **great**. Another meeting.*

            Recognizing these is key at C2: the literal meaning isn't the real message.
            """)],
          [("That's food for thought.", "Eso da que pensar."),
           ("It was a half-baked idea.", "Era una idea a medio cocinar."),
           ("The news was hard to swallow.", "La noticia fue difícil de digerir."),
           ("This report sheds light on the problem.", "Este informe arroja luz sobre el problema."),
           ("It's not exactly cheap.", "No es precisamente barato."),
           ("I'm not the biggest fan of horror movies.", "No soy muy fan de las películas de terror."),
           ("Oh, great. Another meeting.", "Ah, genial. Otra reunión."),
           ("They shot down my proposal.", "Echaron por tierra mi propuesta.")],
          [("food for thought", "algo que da que pensar", "Your comment gave me food for thought."),
           ("half-baked", "mal pensado / a medias", "A half-baked plan."),
           ("hard to swallow", "difícil de aceptar", "The truth is hard to swallow."),
           ("shed light on", "arrojar luz sobre", "Can you shed light on this?"),
           ("shoot down", "echar por tierra", "They shot down every idea."),
           ("understatement", "eufemismo / atenuación", "That's an understatement!"),
           ("irony", "ironía", "The irony is that…"),
           ("not exactly", "no precisamente", "It's not exactly easy."),
           ("figurative", "figurado", "In a figurative sense."),
           ("literal", "literal", "Don't take it literally."),
           ("tone", "tono", "It depends on your tone."),
           ("digest", "digerir / asimilar", "I need time to digest this.")],
          [C("'It's not exactly cheap' really means:", ["It's cheap.", "It's quite expensive.", "It's free."], 1),
           C("'That gives me food for thought' means:", ["I'm hungry.", "It makes me think.", "I disagree."], 1),
           C("A poorly planned idea is ___.", ["half-baked", "hard to swallow", "well-done"], 0),
           C("'Oh, perfect, it's raining again.' The speaker is being:", ["literal", "ironic", "formal"], 1),
           C("To explain something unclear = to ___ on it.", ["shed light", "shoot down", "digest"], 0),
           F("The bad news was hard to ___.", "swallow"),
           F("Calling it 'a bit warm' at 40°C is an ___!", "understatement"),
           F("They shot ___ my proposal in five minutes.", "down"),
           T("Eso da que pensar.", "That's food for thought|That is food for thought|That gives me food for thought")]),
    ],
}
