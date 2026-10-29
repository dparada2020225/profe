from helpers import U, C, F, T, O

LEVEL = {
    "id": "C1",
    "title": "C1 · Avanzado",
    "description": "Te expresas con precisión y estilo, adaptas el registro y manejas estructuras de énfasis.",
    "units": [
        U("c1-01", "Never have I seen…", "Inversion for emphasis",
          "Use inversion to sound emphatic and formal.",
          [("Negative adverbials + inversion", """
            When a sentence starts with a negative or restrictive expression, the auxiliary comes **before** the subject (like a question):

            - **Never** have I seen such a mess.
            - **Rarely / Seldom** do we get a chance like this.
            - **Not only** did she win, **but** she also broke the record.
            - **No sooner** had I arrived **than** it started to rain.
            - **Hardly / Scarcely** had we sat down **when** the phone rang.
            - **Under no circumstances** should you share your password.
            - **Only then / Only when / Only after** … did I understand.
            - **Little** did he know that…
            """),
           ("When to use it", """
            Inversion is typical of **formal writing, speeches and storytelling**. In everyday conversation it can sound dramatic — which is sometimes exactly the effect you want.

            Neutral: *I had never seen anything like it.*
            Emphatic: ***Never had I seen** anything like it.*
            """)],
          [("Never have I seen such a beautiful place.", "Nunca había visto un lugar tan hermoso."),
           ("Not only did he lie, but he also blamed me.", "No solo mintió, sino que además me culpó."),
           ("Rarely do we get an opportunity like this.", "Rara vez tenemos una oportunidad así."),
           ("No sooner had I left than it started raining.", "Apenas había salido cuando empezó a llover."),
           ("Under no circumstances should you open this file.", "Bajo ninguna circunstancia debes abrir este archivo."),
           ("Only then did I realize my mistake.", "Solo entonces me di cuenta de mi error."),
           ("Little did they know what was coming.", "Poco sabían lo que se avecinaba."),
           ("Hardly had we started when the power went out.", "Apenas habíamos empezado cuando se fue la luz.")],
          [("seldom", "rara vez", "Seldom do I eat out."),
           ("no sooner… than", "apenas… cuando", "No sooner had I sat down than…"),
           ("hardly… when", "apenas… cuando", "Hardly had I arrived when…"),
           ("under no circumstances", "bajo ninguna circunstancia", "Under no circumstances…"),
           ("little did I know", "poco sabía yo", "Little did I know she was lying."),
           ("such", "tal / tan", "Such a beautiful day!"),
           ("emphatic", "enfático", "Inversion is emphatic."),
           ("break a record", "romper un récord", "She broke the world record."),
           ("on no account", "de ningún modo", "On no account should you leave."),
           ("not until", "no fue hasta", "Not until later did I understand."),
           ("rarely", "rara vez", "Rarely does it snow here."),
           ("in no way", "de ninguna manera", "In no way is this acceptable.")],
          [C("Never ___ such a delicious meal.", ["I have eaten", "have I eaten", "I ate"], 1),
           C("Not only ___ late, but he also forgot the documents.", ["he was", "was he", "he were"], 1),
           C("No sooner had we arrived ___ the show started.", ["when", "than", "that"], 1),
           C("Under no circumstances ___ the door.", ["you should open", "should you open", "you open"], 1),
           C("Little ___ that she was planning a surprise.", ["did I know", "I knew", "I did know"], 0),
           C("Hardly ___ when the phone rang.", ["I had sat down", "had I sat down", "I sat down"], 1),
           F("Rarely ___ we see such talent.", "do"),
           F("Only after the meeting ___ I understand the problem.", "did"),
           F("Not until I left home ___ I appreciate my parents.", "did"),
           T("Nunca había visto algo así.", "Never had I seen anything like it|Never have I seen anything like it|I had never seen anything like it|Never had I seen anything like that")]),

        U("c1-02", "What I need is…", "Cleft sentences",
          "Highlight the key information in a sentence.",
          [("It-clefts", """
            **It is/was + focus + that/who…** → highlights one element:

            - Neutral: *Ana broke the window.*
            - ***It was Ana who** broke the window.* (not someone else)
            - ***It was the window that** Ana broke.* (not the door)
            - ***It wasn't until** midnight **that** we finished.*
            """),
           ("Wh-clefts (pseudo-clefts)", """
            **What + clause + be + focus** → builds suspense and emphasis:

            > **What I need is** a long holiday.
            > **What surprised me was** how calm she was.
            > **What we did was** (to) call the police.
            > **All I want is** some peace and quiet.
            > **The reason (why) I called is that** I need your help.
            > **The thing is**, I don't have time.
            """)],
          [("What I need is a long vacation.", "Lo que necesito son unas largas vacaciones."),
           ("It was my brother who told me.", "Fue mi hermano quien me lo dijo."),
           ("What surprised me was her reaction.", "Lo que me sorprendió fue su reacción."),
           ("It wasn't until midnight that we finished.", "No fue hasta medianoche que terminamos."),
           ("All I want is to sleep.", "Lo único que quiero es dormir."),
           ("The reason I'm calling is that I need help.", "El motivo de mi llamada es que necesito ayuda."),
           ("What we did was call a mechanic.", "Lo que hicimos fue llamar a un mecánico."),
           ("The thing is, I don't have enough time.", "El asunto es que no tengo suficiente tiempo.")],
          [("the thing is", "el asunto es que", "The thing is, it's too late."),
           ("all I want", "lo único que quiero", "All I want is the truth."),
           ("the reason why", "la razón por la que", "That's the reason why I left."),
           ("surprise", "sorprender", "What surprised me was the price."),
           ("reaction", "reacción", "Her reaction was unexpected."),
           ("focus", "enfoque / centrarse", "Let's focus on the solution."),
           ("emphasize", "enfatizar", "I want to emphasize this point."),
           ("highlight", "resaltar", "The report highlights the risks."),
           ("peace and quiet", "paz y tranquilidad", "I need some peace and quiet."),
           ("annoy", "molestar", "What annoys me is the noise."),
           ("point", "punto / idea", "That's not the point."),
           ("whole", "entero", "The whole point is to learn.")],
          [C("___ I need is a good night's sleep.", ["That", "What", "Which"], 1),
           C("It was Sofia ___ found the bug.", ["what", "who", "which"], 1),
           C("It wasn't until I moved abroad ___ I learned to cook.", ["when", "that", "than"], 1),
           C("___ want is to be left alone.", ["All I", "What all I", "That I"], 0),
           C("Emphasize 'the price': ___", ["What shocked me was the price.", "The price shocked me what.", "It shocked me the price."], 0),
           F("What I like about her ___ her honesty.", "is"),
           F("It ___ my mom who taught me to read. (past)", "was"),
           F("The reason ___ I'm late is the traffic.", "why|that"),
           T("Lo que necesito es tiempo.", "What I need is time|All I need is time"),
           T("Fue mi hermano quien me lo dijo.", "It was my brother who told me|It was my brother that told me")]),

        U("c1-03", "Should you need anything…", "Advanced conditionals",
          "Use formal and alternative conditional structures.",
          [("Inverted conditionals", """
            In formal English, *if* can be dropped and the auxiliary inverted:

            - ***Should** you need help, call me.* = If you should need help…
            - ***Were** I in your position, I would resign.* = If I were…
            - ***Had** I known, I would have come.* = If I had known…
            """),
           ("Alternatives to if", """
            - **provided / providing (that)**, **as long as**, **on condition that** → *You can borrow it **as long as** you return it.*
            - **suppose / supposing / what if** → ***Supposing** you lost your job, what would you do?*
            - **otherwise** → *Hurry up, **otherwise** we'll be late.*
            - **but for / if it weren't for / if it hadn't been for** → ***But for** your help, I would have failed.*
            - **were to** (hypothetical future) → *If the company **were to** close, many people would lose their jobs.*
            """)],
          [("Should you need anything, let me know.", "Si necesitas algo, avísame."),
           ("Had I known, I would have told you.", "De haberlo sabido, te lo habría dicho."),
           ("Were I you, I'd accept the offer.", "Yo que tú, aceptaría la oferta."),
           ("You can go as long as you're back by ten.", "Puedes ir siempre y cuando regreses antes de las diez."),
           ("But for your help, I would have failed.", "De no ser por tu ayuda, habría fracasado."),
           ("Supposing you won, what would you do?", "Suponiendo que ganaras, ¿qué harías?"),
           ("If it weren't for my family, I'd move abroad.", "Si no fuera por mi familia, me iría al extranjero."),
           ("If prices were to rise, sales would fall.", "Si los precios subieran, las ventas caerían.")],
          [("provided that", "siempre que", "I'll help, provided that you ask nicely."),
           ("as long as", "siempre y cuando", "As long as you're happy."),
           ("on condition that", "con la condición de que", "On condition that it's legal."),
           ("supposing", "suponiendo que", "Supposing it rains?"),
           ("but for", "de no ser por", "But for the map, we'd be lost."),
           ("otherwise", "de lo contrario", "Leave now, otherwise you'll be late."),
           ("resign", "renunciar (formal)", "She resigned last week."),
           ("in your position", "en tu lugar", "In your position, I'd wait."),
           ("unless", "a menos que", "Unless you object, we'll start."),
           ("even if", "aunque / aun si", "Even if it rains, we'll go."),
           ("whether or not", "sea como sea / ya sea que", "Whether or not you agree…"),
           ("hypothetical", "hipotético", "It's a hypothetical question.")],
          [C("___ you have any questions, please contact us.", ["If", "Should", "Would"], 1),
           C("___ I known, I would have helped.", ["Had", "If", "Have"], 0),
           C("You can borrow my car ___ you drive carefully.", ["unless", "as long as", "otherwise"], 1),
           C("___ your advice, I would have made a mistake.", ["But for", "Unless", "Provided"], 0),
           C("If the government ___ raise taxes, people would protest.", ["was to", "were to", "would"], 1),
           F("___ I in your position, I would resign.", "Were"),
           F("Hurry, ___ we'll miss the train.", "otherwise|or"),
           F("If it ___ (not) for you, I'd still be lost.", "weren't|were not|wasn't"),
           T("Si necesitas algo, avísame.", "Should you need anything, let me know|If you need anything, let me know|Should you need anything, tell me"),
           T("De no ser por tu ayuda, habría fracasado.", "But for your help, I would have failed|If it hadn't been for your help, I would have failed|Without your help, I would have failed")]),

        U("c1-04", "It would appear that…", "Hedging and softening",
          "Express opinions cautiously and diplomatically, especially in academic and professional contexts.",
          [("Why hedge?", """
            In academic writing, meetings and emails, strong statements can sound arrogant or inaccurate. **Hedging** shows caution and politeness.

            - Strong: *This method is better.*
            - Hedged: *This method **appears to be** somewhat more effective.*
            """),
           ("Tools for hedging", """
            - Verbs: **seem, appear, tend to, suggest, indicate** → *The data **suggests** that…*
            - Modals: **may, might, could, would** → *This **could** be due to…*
            - Adverbs: **possibly, arguably, apparently, relatively, to some extent**
            - Phrases: ***It would appear that…***, ***There is some evidence that…***, ***It is likely that…***
            - Softeners in speech: ***I'm not sure, but…***, ***I'd say…***, ***Correct me if I'm wrong, but…***, ***I was wondering if…***
            """)],
          [("It would appear that the results are inconclusive.", "Al parecer, los resultados no son concluyentes."),
           ("The data suggests a possible link.", "Los datos sugieren un posible vínculo."),
           ("This could be due to several factors.", "Esto podría deberse a varios factores."),
           ("Users tend to prefer simpler interfaces.", "Los usuarios tienden a preferir interfaces más simples."),
           ("Correct me if I'm wrong, but I think there's a bug.", "Corrígeme si me equivoco, pero creo que hay un bug."),
           ("I was wondering if you could help me.", "Me preguntaba si podrías ayudarme."),
           ("It is arguably the best solution.", "Es posiblemente la mejor solución."),
           ("To some extent, I agree with you.", "Hasta cierto punto, estoy de acuerdo contigo.")],
          [("seem", "parecer", "It seems to work."),
           ("appear", "parecer (formal)", "It appears that…"),
           ("tend to", "tender a", "People tend to forget."),
           ("arguably", "posiblemente / se podría decir", "Arguably the best."),
           ("to some extent", "hasta cierto punto", "To some extent, yes."),
           ("likely", "probable", "It's likely to fail."),
           ("indicate", "indicar", "The results indicate that…"),
           ("inconclusive", "no concluyente", "The test was inconclusive."),
           ("link", "vínculo / relación", "There's a link between sleep and memory."),
           ("factor", "factor", "Several factors are involved."),
           ("relatively", "relativamente", "It's relatively cheap."),
           ("assume", "suponer / asumir", "I assume you've read it.")],
          [C("Most cautious option:", ["This proves the theory.", "This seems to support the theory.", "This is the theory."], 1),
           C("The results ___ that the drug is effective.", ["suggest", "are proving", "know"], 0),
           C("Polite request at work:", ["Send me the file.", "I was wondering if you could send me the file.", "You must send me the file."], 1),
           C("It is ___ that prices will rise.", ["likely", "likelihood", "like"], 0),
           C("Users ___ to ignore long messages.", ["tend", "seem that", "appear that"], 0),
           F("To some ___, I agree with your point.", "extent"),
           F("It would ___ that the server is down.", "appear|seem"),
           F("This ___ be due to a network error. (posibilidad)", "could|may|might"),
           T("Me preguntaba si podrías ayudarme.", "I was wondering if you could help me|I was wondering whether you could help me"),
           T("Hasta cierto punto, estoy de acuerdo.", "To some extent, I agree|To a certain extent, I agree|To some extent I agree")]),

        U("c1-05", "The implementation of…", "Nominalisation and formal register",
          "Write in a formal, academic or professional style.",
          [("Nominalisation", """
            Formal English often turns **verbs and adjectives into nouns**, making text more objective and dense:

            - *We **analyzed** the data and **discovered** that…* → *An **analysis** of the data led to the **discovery** that…*
            - *Because prices **increased** quickly…* → *Due to the rapid **increase** in prices…*
            - *If the team **is not available**…* → *In the event of the team's **unavailability**…*
            """),
           ("Formal vs informal vocabulary", """
            - get → **obtain / receive** · buy → **purchase** · ask for → **request**
            - help → **assist** · need → **require** · start → **commence / initiate**
            - find out → **discover / ascertain** · enough → **sufficient** · about → **approximately**
            - but → **however / nevertheless** · so → **therefore / consequently**

            Avoid contractions, phrasal verbs and vague words (*things, stuff, a lot of*) in formal writing.
            """)],
          [("The implementation of the new system took six months.", "La implementación del nuevo sistema tomó seis meses."),
           ("Due to the rapid increase in costs, the project was delayed.", "Debido al rápido aumento de costos, el proyecto se retrasó."),
           ("We would like to request further information.", "Nos gustaría solicitar más información."),
           ("Applicants are required to submit two references.", "Los solicitantes deben presentar dos referencias."),
           ("An analysis of the data revealed several errors.", "Un análisis de los datos reveló varios errores."),
           ("Please do not hesitate to contact us.", "No dude en contactarnos."),
           ("The decision was made following a thorough review.", "La decisión se tomó tras una revisión exhaustiva."),
           ("Approximately half of the participants agreed.", "Aproximadamente la mitad de los participantes estuvo de acuerdo.")],
          [("implementation", "implementación", "The implementation was successful."),
           ("purchase", "compra / comprar", "Thank you for your purchase."),
           ("request", "solicitar / solicitud", "We request your attendance."),
           ("require", "requerir", "This position requires experience."),
           ("assist", "asistir / ayudar", "How may I assist you?"),
           ("obtain", "obtener", "You must obtain a permit."),
           ("sufficient", "suficiente", "There is sufficient evidence."),
           ("thorough", "exhaustivo", "A thorough review."),
           ("submit", "presentar / entregar", "Submit your application."),
           ("consequently", "en consecuencia", "Consequently, sales fell."),
           ("regarding", "con respecto a", "Regarding your request…"),
           ("hesitate", "dudar", "Don't hesitate to call.")],
          [C("Formal version of 'buy':", ["get", "purchase", "grab"], 1),
           C("Formal version of 'help':", ["assist", "give a hand", "back up"], 0),
           C("Nominalised: 'Prices increased rapidly, so…'", ["Due to the rapid increase in prices, …", "Because prices are going up fast, …", "Prices went up a lot, so…"], 0),
           C("Most formal email closing:", ["Text me if you need stuff.", "Please do not hesitate to contact me.", "Hit me up!"], 1),
           C("This position ___ at least three years of experience.", ["needs", "requires", "wants"], 1),
           F("We would like to ___ further information. (pedir, formal)", "request"),
           F("___ to the storm, the event was postponed.", "Due|Owing"),
           F("___ your request, we are pleased to inform you…", "Regarding|Concerning"),
           T("Nos gustaría solicitar más información.", "We would like to request further information|We would like to request more information"),
           T("No dude en contactarnos.", "Please do not hesitate to contact us|Do not hesitate to contact us|Don't hesitate to contact us")]),

        U("c1-06", "She's bound to…", "Advanced ways to express probability",
          "Express degrees of certainty with precision.",
          [("Certainty scale", """
            - **bound to / sure to / certain to** → almost certain → *It's **bound to** rain; it always does in October.*
            - **(highly) likely to / the chances are (that)** → probable → *She's **likely to** get the promotion.*
            - **may well / could well** → quite possible → *Prices **may well** rise again.*
            - **unlikely to / there's little chance of** → improbable → *He's **unlikely to** agree.*
            - **there's no way** (informal) → impossible → ***There's no way** we'll finish today.*
            """),
           ("Useful nouns and adjectives", """
            - **a good chance / a slim chance**: *There's a slim chance we'll win.*
            - **in all likelihood** = very probably
            - **odds**: *The odds are against us.*
            - **inevitable**, **doubtful**, **conceivable**
            """)],
          [("It's bound to rain this afternoon.", "Seguro que llueve esta tarde."),
           ("She's likely to get the promotion.", "Es probable que ella obtenga el ascenso."),
           ("Prices may well go up again.", "Es muy posible que los precios vuelvan a subir."),
           ("He's unlikely to change his mind.", "Es poco probable que cambie de opinión."),
           ("There's no way we'll finish today.", "No hay manera de que terminemos hoy."),
           ("In all likelihood, the meeting will be postponed.", "Con toda probabilidad, la reunión se pospondrá."),
           ("The chances are that he's already left.", "Lo más probable es que ya se haya ido."),
           ("There's a slim chance of rain.", "Hay una pequeña posibilidad de lluvia.")],
          [("bound to", "seguro que (va a)", "You're bound to pass."),
           ("likely", "probable", "It's likely to happen."),
           ("unlikely", "improbable", "It's unlikely to work."),
           ("may well", "muy bien podría", "It may well be true."),
           ("the chances are", "lo más probable es que", "The chances are he forgot."),
           ("slim chance", "posibilidad remota", "We have a slim chance."),
           ("in all likelihood", "con toda probabilidad", "In all likelihood, yes."),
           ("odds", "probabilidades", "The odds are in our favor."),
           ("inevitable", "inevitable", "Change is inevitable."),
           ("doubtful", "dudoso", "It's doubtful he'll come."),
           ("promotion", "ascenso", "She got a promotion."),
           ("postpone", "posponer", "The launch was postponed.")],
          [C("Almost certain:", ["She's bound to win.", "She may win.", "She's unlikely to win."], 0),
           C("He works so hard; he's ___ to succeed.", ["unlikely", "bound", "doubtful"], 1),
           C("It's ___ that he'll come — he's very busy.", ["doubtful", "bound", "certain to"], 0),
           C("The ___ are that she's still at work.", ["chances", "likely", "bounds"], 0),
           C("Prices ___ well rise next year.", ["may", "are", "bound"], 0),
           F("There's a ___ chance of snow. (pequeña)", "slim|small"),
           F("In all ___, the flight will be delayed.", "likelihood|probability"),
           F("She's ___ to agree; she never does. (improbable)", "unlikely"),
           T("Es poco probable que cambie de opinión.", "He's unlikely to change his mind|He is unlikely to change his mind|She's unlikely to change her mind|It's unlikely that he'll change his mind"),
           T("Seguro que llueve.", "It's bound to rain|It is bound to rain|It's sure to rain")]),

        U("c1-07", "I think so / I hope not", "Ellipsis and substitution",
          "Avoid repetition the way native speakers do.",
          [("Substitution", """
            - **so / not** replace a clause: *Is it going to rain? — I think **so**. / I hope **not**.* (also: I suppose so, I'm afraid not, I guess so)
            - **one / ones** replace countable nouns: *I don't like this shirt; I prefer the blue **one**.*
            - **do so** (formal) replaces a verb phrase: *He promised to call, but he didn't **do so**.*
            """),
           ("Ellipsis", """
            Leave out words that are obvious from context:

            - *I wanted to go, but I couldn't (go).*
            - *She can speak French and so **can** I.* / *I'm not tired and neither **is** she.*
            - *"Have you finished?" "Not yet (finished)."*
            - *I'd love **to** (come).* — keep **to**.
            - *"Are you coming?" "I might (come)."*
            """)],
          [("Is it going to rain? — I hope not.", "¿Va a llover? — Espero que no."),
           ("Will you be there? — I think so.", "¿Vas a estar ahí? — Creo que sí."),
           ("I don't like the red one; I prefer the black one.", "No me gusta el rojo; prefiero el negro."),
           ("I wanted to help, but I couldn't.", "Quería ayudar, pero no pude."),
           ("She loves sushi and so do I.", "A ella le encanta el sushi y a mí también."),
           ("I don't smoke and neither does my brother.", "Yo no fumo y mi hermano tampoco."),
           ("Would you like to come? — I'd love to.", "¿Te gustaría venir? — Me encantaría."),
           ("He said he'd fix it, but he hasn't done so.", "Dijo que lo arreglaría, pero no lo ha hecho.")],
          [("I think so", "creo que sí", "Is it right? — I think so."),
           ("I hope not", "espero que no", "Will it be expensive? — I hope not."),
           ("I'm afraid not", "me temo que no", "Is there a table? — I'm afraid not."),
           ("so do I", "yo también", "I love it. — So do I."),
           ("neither do I", "yo tampoco", "I don't care. — Neither do I."),
           ("one / ones", "uno / unos (sustituto)", "Which one do you want?"),
           ("do so", "hacerlo (formal)", "If you wish to do so, …"),
           ("I'd love to", "me encantaría", "Coffee? — I'd love to."),
           ("not yet", "todavía no", "Ready? — Not yet."),
           ("I suppose so", "supongo que sí", "Can we leave? — I suppose so."),
           ("either", "tampoco (al final)", "I can't swim either."),
           ("avoid repetition", "evitar repetición", "Ellipsis helps avoid repetition.")],
          [C("Is the shop open? — I'm afraid ___.", ["no", "not", "so"], 1),
           C("I like this laptop, but the silver ___ is cheaper.", ["one", "it", "ones"], 0),
           C("I can't dance. — Neither ___ I.", ["can", "do", "am"], 0),
           C("She has finished and so ___ I.", ["did", "have", "do"], 1),
           C("Do you want to come? — I'd love ___.", ["it", "to", "so"], 1),
           C("Will it be ready by Friday? — I hope ___.", ["so", "it", "yes"], 0),
           F("I'm not hungry and neither ___ Ana.", "is"),
           F("He promised to apologize, but he never did ___.", "so"),
           T("Yo no fumo y mi hermano tampoco.", "I don't smoke and neither does my brother|I don't smoke and my brother doesn't either|I don't smoke, and neither does my brother"),
           T("¿Va a llover? — Espero que no.", "Is it going to rain? — I hope not|Is it going to rain? I hope not|Will it rain? I hope not")]),

        U("c1-08", "Let's get the ball rolling", "Idioms and fixed expressions for discussion",
          "Use idiomatic language naturally in meetings and conversations.",
          [("At work and in meetings", """
            - **get the ball rolling** = start something
            - **be on the same page** = agree / understand the same thing
            - **touch base** = make brief contact
            - **think outside the box** = think creatively
            - **cut corners** = do something badly to save time/money
            - **back to square one** = start again from the beginning
            - **a win-win situation** = good for everyone
            """),
           ("In conversation", """
            - **it's not my cup of tea** = I don't really like it
            - **hit the nail on the head** = describe exactly right
            - **beat around the bush** = avoid the main topic
            - **a piece of cake** = very easy
            - **take it with a grain of salt** = don't believe it completely
            - **once in a blue moon** = very rarely
            - **the elephant in the room** = an obvious problem nobody mentions

            Use idioms sparingly — too many sound unnatural.
            """)],
          [("Let's get the ball rolling.", "Empecemos."),
           ("I want to make sure we're on the same page.", "Quiero asegurarme de que estamos en la misma sintonía."),
           ("Let's touch base next week.", "Hablemos brevemente la próxima semana."),
           ("Don't cut corners on security.", "No escatimes en seguridad."),
           ("The deal fell through, so we're back to square one.", "El trato fracasó, así que volvimos al punto de partida."),
           ("You hit the nail on the head.", "Diste en el clavo."),
           ("Stop beating around the bush and tell me.", "Deja de andarte por las ramas y dímelo."),
           ("Jazz isn't really my cup of tea.", "El jazz no es lo mío.")],
          [("get the ball rolling", "poner en marcha", "Who wants to get the ball rolling?"),
           ("on the same page", "en la misma sintonía", "Are we on the same page?"),
           ("touch base", "ponerse en contacto brevemente", "I'll touch base with you tomorrow."),
           ("cut corners", "escatimar / hacer a medias", "They cut corners and it failed."),
           ("back to square one", "volver a empezar", "We're back to square one."),
           ("hit the nail on the head", "dar en el clavo", "Exactly — you hit the nail on the head."),
           ("beat around the bush", "andarse por las ramas", "Don't beat around the bush."),
           ("a piece of cake", "pan comido", "The exam was a piece of cake."),
           ("not my cup of tea", "no es lo mío", "Horror films aren't my cup of tea."),
           ("once in a blue moon", "muy de vez en cuando", "I eat fast food once in a blue moon."),
           ("the elephant in the room", "el problema evidente que nadie menciona", "Let's address the elephant in the room."),
           ("a grain of salt", "con reservas", "Take it with a grain of salt.")],
          [C("'Very easy' =", ["a piece of cake", "a cup of tea", "square one"], 0),
           C("The project failed and we have to start again. We're ___.", ["on the same page", "back to square one", "cutting corners"], 1),
           C("'Describe exactly right' =", ["beat around the bush", "hit the nail on the head", "touch base"], 1),
           C("I rarely go to the cinema — only once in a ___.", ["blue moon", "full moon", "red moon"], 0),
           C("'Avoid the main topic' =", ["beat around the bush", "think outside the box", "get the ball rolling"], 0),
           F("Let's get the ball ___.", "rolling"),
           F("Before we continue, are we all on the same ___?", "page"),
           F("Don't cut ___ on quality.", "corners"),
           T("Diste en el clavo.", "You hit the nail on the head"),
           T("No es lo mío.", "It's not my cup of tea|It isn't my cup of tea|It's not really my cup of tea")]),
    ],
}
