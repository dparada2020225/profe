from helpers import U, C, F, T, O

LEVEL = {
    "id": "B2",
    "title": "B2 · Intermedio alto",
    "description": "Hablas con fluidez sobre temas complejos, argumentas y entiendes matices. Explicaciones casi todo en inglés.",
    "units": [
        U("b2-01", "By the time I arrived…", "Narrative tenses: past perfect (simple & continuous)",
          "Tell stories clearly, showing what happened first.",
          [("Past perfect simple", """
            **had + past participle** → an action that happened **before** another past action.

            > When I got to the station, the train **had** already **left**.
            > She was nervous because she **hadn't flown** before.

            Without it, the order changes: *When I arrived, the train left* = it left when I arrived.

            🇪🇸 Equivale a "había + participio": *ya se había ido*.
            """),
           ("Past perfect continuous", """
            **had been + -ing** → a longer activity that continued up to a point in the past.

            > I **had been waiting** for an hour when she finally called.
            > His eyes were red because he **had been crying**.
            """),
           ("Narrative tenses together", """
            - **Past simple**: main events → *I opened the door.*
            - **Past continuous**: background → *It was raining.*
            - **Past perfect**: earlier events → *Someone had broken the window.*
            """)],
          [("When I arrived, the meeting had already started.", "Cuando llegué, la reunión ya había empezado."),
           ("I had never seen the sea before that trip.", "Nunca había visto el mar antes de ese viaje."),
           ("She had been working there for years before she quit.", "Llevaba años trabajando ahí antes de renunciar."),
           ("By the time we got there, everyone had left.", "Para cuando llegamos, todos se habían ido."),
           ("He was exhausted because he had been studying all night.", "Estaba agotado porque había estado estudiando toda la noche."),
           ("I realized I had forgotten my passport.", "Me di cuenta de que había olvidado mi pasaporte."),
           ("They hadn't eaten anything since breakfast.", "No habían comido nada desde el desayuno."),
           ("It was raining and the streets were empty.", "Estaba lloviendo y las calles estaban vacías.")],
          [("by the time", "para cuando", "By the time I woke up, she had left."),
           ("realize", "darse cuenta", "I realized I was wrong."),
           ("already", "ya", "They had already eaten."),
           ("passport", "pasaporte", "I forgot my passport."),
           ("nervous", "nervioso", "I was nervous before the interview."),
           ("finally", "por fin", "She finally called."),
           ("empty", "vacío", "The office was empty."),
           ("break into", "entrar a robar", "Someone had broken into the house."),
           ("turn out", "resultar", "It turned out he had lied."),
           ("previously", "previamente", "I had previously worked in sales."),
           ("plot", "trama", "The plot was confusing."),
           ("flashback", "escena retrospectiva", "The movie has many flashbacks.")],
          [C("When I got to the airport, my flight ___.", ["already left", "had already left", "has already left"], 1),
           C("She was tired because she ___ all day.", ["had been working", "has worked", "was working"], 0),
           C("By the time the police arrived, the thieves ___.", ["escaped", "had escaped", "have escaped"], 1),
           C("I ___ to Europe before 2024.", ["had never been", "have never been", "never was"], 0),
           C("Which shows the order clearly? 'The film ___ when we arrived, so we missed the start.'", ["started", "had started", "was starting"], 1),
           F("I realized I ___ (leave) my keys at work.", "had left|'d left"),
           F("They ___ (wait) for two hours when the bus finally came.", "had been waiting|'d been waiting"),
           F("___ the time we arrived, the party was over.", "By"),
           T("Cuando llegué, ya se habían ido.", "When I arrived, they had already left|When I got there, they had already left|When I arrived, they'd already left|When I arrived they had already left"),
           T("Nunca había visto el mar.", "I had never seen the sea|I'd never seen the sea|I had never seen the ocean")]),

        U("b2-02", "If I had known…", "Third and mixed conditionals",
          "Talk about imaginary past situations and regrets.",
          [("Third conditional", """
            **If + past perfect, would have + past participle** → an imaginary **past** (it didn't happen).

            > If I **had studied**, I **would have passed**. (I didn't study, I didn't pass.)
            > If you **had told** me, I **could have helped**.
            > We **wouldn't have got** lost if we **had taken** the map.

            🇪🇸 *Si hubiera estudiado, habría aprobado.*
            """),
           ("Mixed conditionals", """
            Mixing times:

            - Past condition → present result: *If I **had taken** that job, I **would be** rich now.*
            - Present condition → past result: *If I **were** more organized, I **wouldn't have missed** the deadline.*
            """),
           ("Pronunciation", """
            In speech, **would have** sounds like *would've* /ˈwʊdəv/; **had** becomes *'d*:
            *If I'd known, I would've come.*
            Never write *would of* — it's a common mistake even for native speakers.
            """)],
          [("If I had known, I would have come.", "Si lo hubiera sabido, habría venido."),
           ("If you had told me, I could have helped.", "Si me hubieras dicho, te habría podido ayudar."),
           ("We wouldn't have missed the flight if we had left earlier.", "No habríamos perdido el vuelo si hubiéramos salido antes."),
           ("If I had taken that job, I would be living in Canada now.", "Si hubiera aceptado ese trabajo, ahora estaría viviendo en Canadá."),
           ("What would you have done?", "¿Qué habrías hecho?"),
           ("If she hadn't helped me, I would have failed.", "Si ella no me hubiera ayudado, habría reprobado."),
           ("If I were taller, I would have joined the team.", "Si fuera más alto, habría entrado al equipo."),
           ("I would have called, but my phone died.", "Habría llamado, pero se me murió el teléfono.")],
          [("regret", "arrepentirse / arrepentimiento", "I regret not studying more."),
           ("deadline", "fecha límite", "We missed the deadline."),
           ("opportunity", "oportunidad", "I missed a great opportunity."),
           ("mistake", "error", "It was a big mistake."),
           ("otherwise", "de lo contrario", "I left early; otherwise I'd have been late."),
           ("get lost", "perderse", "We got lost in the city."),
           ("in time", "a tiempo (con margen)", "We arrived just in time."),
           ("decision", "decisión", "It was the right decision."),
           ("consequence", "consecuencia", "Every action has consequences."),
           ("lucky", "con suerte", "We were lucky."),
           ("blame", "culpar", "Don't blame yourself."),
           ("die (battery)", "morirse (batería)", "My phone died.")],
          [C("If I ___ harder, I would have passed.", ["studied", "had studied", "would study"], 1),
           C("If you had asked me, I ___ you.", ["would help", "would have helped", "had helped"], 1),
           C("If I had saved money, I ___ a car now.", ["would have", "would have had", "had"], 0, "Mixed: past condition → present result."),
           C("Correct spelling:", ["I would of gone.", "I would have gone.", "I would had gone."], 1),
           C("She wouldn't have been late if she ___ the bus.", ["didn't miss", "hadn't missed", "wouldn't miss"], 1),
           F("If we ___ (leave) earlier, we wouldn't have missed the flight.", "had left|'d left"),
           F("What would you have ___ (do) in my situation?", "done"),
           T("Si lo hubiera sabido, habría venido.", "If I had known, I would have come|If I'd known, I would have come|If I had known, I'd have come|If I had known I would have come"),
           T("¿Qué habrías hecho?", "What would you have done?")]),

        U("b2-03", "I wish I had…", "Wishes and regrets",
          "Express wishes about the present and regrets about the past.",
          [("Present wishes", """
            **wish / if only + past simple** → you'd like the present to be different.

            > I **wish** I **had** more free time. (I don't have it)
            > I wish I **were** taller.
            > If only I **knew** the answer!
            """),
           ("Past regrets", """
            **wish / if only + past perfect** → regret about the past.

            > I wish I **had studied** medicine.
            > If only I **hadn't said** that!
            """),
           ("Wish + would", """
            **wish + would** → complaints about someone else's behavior or something you want to change:

            > I wish you **would stop** interrupting me.
            > I wish it **would stop** raining.

            Not about yourself: *I wish I would…* ❌ → *I wish I could…* ✅

            🇪🇸 *Ojalá / Me gustaría que…*
            """)],
          [("I wish I had more time.", "Ojalá tuviera más tiempo."),
           ("I wish I hadn't said that.", "Ojalá no hubiera dicho eso."),
           ("If only I could speak French!", "¡Si tan solo pudiera hablar francés!"),
           ("I wish you would listen to me.", "Ojalá me escucharas."),
           ("She wishes she had studied abroad.", "Ella desearía haber estudiado en el extranjero."),
           ("I wish it would stop raining.", "Ojalá dejara de llover."),
           ("I wish I were on the beach right now.", "Ojalá estuviera en la playa ahora mismo."),
           ("Don't you wish you had gone?", "¿No te gustaría haber ido?")],
          [("wish", "desear / ojalá", "I wish I could help."),
           ("if only", "si tan solo", "If only I had known."),
           ("complain", "quejarse", "Stop complaining!"),
           ("interrupt", "interrumpir", "Please don't interrupt."),
           ("free time", "tiempo libre", "I need more free time."),
           ("apologize", "disculparse", "I should apologize."),
           ("annoying", "molesto, irritante", "That noise is annoying."),
           ("mess", "desorden / lío", "What a mess!"),
           ("rather", "preferiblemente", "I'd rather stay home."),
           ("patient", "paciente", "I wish I were more patient."),
           ("fluent", "fluido", "I wish I were fluent in English."),
           ("change one's mind", "cambiar de opinión", "I wish he would change his mind.")],
          [C("I wish I ___ a car. (now)", ["have", "had", "would have"], 1),
           C("I wish I ___ that email. (regret)", ["didn't send", "hadn't sent", "wouldn't send"], 1),
           C("I wish you ___ making that noise!", ["would stop", "stopped", "had stopped"], 0),
           C("If only I ___ the answer! (present)", ["know", "knew", "had known"], 1),
           C("Correct:", ["I wish I would be taller.", "I wish I were taller.", "I wish I am taller."], 1),
           F("I wish I ___ (can) speak Japanese.", "could"),
           F("She wishes she ___ (not / quit) her job.", "hadn't quit|had not quit"),
           T("Ojalá tuviera más tiempo.", "I wish I had more time"),
           T("Ojalá no hubiera dicho eso.", "I wish I hadn't said that|I wish I had not said that|If only I hadn't said that")]),

        U("b2-04", "I had my car fixed", "Advanced passive and causative",
          "Use passive structures common in news and formal English, and the causative.",
          [("Passive in all tenses", """
            - Present continuous: *The road **is being repaired**.*
            - Present perfect: *The results **have been published**.*
            - Future / modals: *It **will be done** tomorrow. It **must be signed**.*
            - Infinitive / gerund: *I want **to be told**. I hate **being interrupted**.*
            """),
           ("Have / get something done", """
            When someone else does something **for you** (usually a service):

            > I **had my car fixed**. (A mechanic fixed it.)
            > She's **getting her hair cut** tomorrow.
            > We **had our house painted** last year.

            🇪🇸 *Me cortaron el pelo* → **I had my hair cut** (not *I cut my hair*, which means you did it yourself).
            """),
           ("It is said that…", """
            Reporting passive (news, formal):

            > **It is said that** the hotel is haunted. → The hotel **is said to be** haunted.
            > **It is believed that** he left the country. → He **is believed to have left** the country.
            """)],
          [("The road is being repaired.", "Están reparando la carretera."),
           ("The results have been published.", "Los resultados han sido publicados."),
           ("I had my car fixed yesterday.", "Me arreglaron el carro ayer."),
           ("She's getting her hair cut on Friday.", "Le van a cortar el pelo el viernes."),
           ("It is said that the house is haunted.", "Se dice que la casa está embrujada."),
           ("He is believed to have left the country.", "Se cree que él salió del país."),
           ("The documents must be signed today.", "Los documentos deben firmarse hoy."),
           ("I hate being interrupted.", "Odio que me interrumpan.")],
          [("repair", "reparar", "The bridge is being repaired."),
           ("sign", "firmar", "Please sign here."),
           ("haunted", "embrujado", "That house is haunted."),
           ("hair cut", "corte de pelo", "I need to get my hair cut."),
           ("check", "revisar", "I had my eyes checked."),
           ("deliver", "entregar (a domicilio)", "The package was delivered."),
           ("approve", "aprobar (autorizar)", "The budget has been approved."),
           ("release", "lanzar / publicar", "The app will be released in May."),
           ("arrest", "arrestar", "Two men have been arrested."),
           ("report", "reportar / informar", "It is reported that…"),
           ("estimate", "estimar", "It is estimated that 2 million people…"),
           ("rumor", "rumor", "Rumor has it that…")],
          [C("The bridge ___ at the moment.", ["is repaired", "is being repaired", "has repaired"], 1),
           C("Me arreglaron la laptop:", ["I fixed my laptop.", "I had my laptop fixed.", "I had fixed my laptop."], 1),
           C("The new version ___ next week.", ["will release", "will be released", "is releasing"], 1),
           C("He is believed ___ the money.", ["to steal", "to have stolen", "stealing"], 1),
           C("I don't like ___ what to do.", ["telling", "being told", "to tell"], 1),
           F("The results have ___ (publish) online.", "been published"),
           F("We're getting our house ___ (paint) next month.", "painted"),
           F("It ___ said that he is a millionaire.", "is"),
           T("Me cortaron el pelo ayer.", "I had my hair cut yesterday|I got my hair cut yesterday|I had a haircut yesterday|I got a haircut yesterday"),
           T("Se dice que la casa está embrujada.", "It is said that the house is haunted|It's said that the house is haunted|The house is said to be haunted")]),

        U("b2-05", "She asked me where…", "Reported questions and reporting verbs",
          "Report questions, requests and advice naturally.",
          [("Reported questions", """
            Use **normal word order** (no inversion, no do/did):

            > "Where **do you** live?" → She asked me where **I lived**.
            > "**Did you** see it?" → He asked **if/whether** I had seen it.

            ❌ *She asked me where did I live.*
            """),
           ("Reporting verbs", """
            Instead of always using *said*, use more precise verbs:

            - **verb + to-infinitive**: agree, offer, promise, refuse, threaten → *He **offered to drive** me home.*
            - **verb + someone + to-infinitive**: ask, tell, advise, remind, warn, persuade → *She **reminded me to call** my mom.*
            - **verb + -ing**: admit, deny, suggest, recommend → *He **denied taking** the money.*
            - **verb + preposition + -ing**: apologize for, insist on, accuse someone of → *She **apologized for being** late.*
            """)],
          [("She asked me where I lived.", "Me preguntó dónde vivía."),
           ("He asked if I had finished.", "Preguntó si había terminado."),
           ("They wanted to know when the meeting was.", "Querían saber cuándo era la reunión."),
           ("My boss told me to send the report.", "Mi jefe me dijo que enviara el informe."),
           ("She advised me to rest.", "Me aconsejó que descansara."),
           ("He denied breaking the window.", "Negó haber roto la ventana."),
           ("She apologized for being late.", "Se disculpó por llegar tarde."),
           ("He offered to help me move.", "Se ofreció a ayudarme a mudarme.")],
          [("whether", "si (condicional, formal)", "I don't know whether to go."),
           ("advise", "aconsejar", "I advise you to wait."),
           ("remind", "recordar (a alguien)", "Remind me to buy milk."),
           ("warn", "advertir", "I warned you!"),
           ("deny", "negar", "He denied everything."),
           ("admit", "admitir", "She admitted her mistake."),
           ("threaten", "amenazar", "He threatened to quit."),
           ("persuade", "persuadir", "I persuaded her to come."),
           ("insist on", "insistir en", "He insisted on paying."),
           ("accuse of", "acusar de", "They accused him of lying."),
           ("recommend", "recomendar", "I recommend reading the book first."),
           ("request", "solicitud / solicitar", "I sent a request.")],
          [C("\"Where do you work?\" → She asked me where ___.", ["do I work", "I worked", "did I work"], 1),
           C("\"Are you coming?\" → He asked ___ I was coming.", ["that", "if", "do"], 1),
           C("She ___ me to call my mom.", ["reminded", "suggested", "said"], 0),
           C("He denied ___ the money.", ["to take", "taking", "take"], 1),
           C("She apologized ___ late.", ["to be", "for being", "of being"], 1),
           C("They offered ___ us.", ["to help", "helping", "help"], 0),
           F("My teacher advised me ___ (practice) every day.", "to practice|to practise"),
           F("He insisted ___ paying for dinner.", "on"),
           T("Me preguntó dónde vivía.", "She asked me where I lived|He asked me where I lived"),
           T("Se disculpó por llegar tarde.", "He apologized for being late|She apologized for being late|He apologised for being late|She apologised for being late")]),

        U("b2-06", "This time next year", "Future continuous and future perfect",
          "Talk about actions in progress or completed at a future time.",
          [("Future continuous", """
            **will be + -ing** → an action **in progress** at a future moment.

            > This time tomorrow, I**'ll be flying** to Cancún.
            > Don't call at 8 — I**'ll be having** dinner.

            Also polite questions about plans: ***Will you be using** the car tonight?*
            """),
           ("Future perfect", """
            **will have + past participle** → something **completed before** a future moment.

            > By 2028, I**'ll have graduated**.
            > By the time you arrive, we**'ll have finished**.

            Typical: **by** + time (para / antes de), **by the time**, **in** two years' time.
            """)],
          [("This time tomorrow, I'll be traveling.", "Mañana a esta hora estaré viajando."),
           ("I'll be working late tonight.", "Esta noche estaré trabajando hasta tarde."),
           ("By 2028, I'll have graduated.", "Para 2028 ya me habré graduado."),
           ("Will you be using your laptop later?", "¿Vas a estar usando tu laptop más tarde?"),
           ("By the time you get here, we'll have eaten.", "Para cuando llegues, ya habremos comido."),
           ("In ten years, they'll have built a new airport.", "En diez años habrán construido un nuevo aeropuerto."),
           ("I'll have finished the project by Friday.", "Habré terminado el proyecto para el viernes."),
           ("Don't call at nine; I'll be sleeping.", "No llames a las nueve; estaré durmiendo.")],
          [("by (time)", "para (a más tardar)", "Send it by Monday."),
           ("by the time", "para cuando", "By the time you read this…"),
           ("this time next year", "el próximo año a esta hora", "This time next year I'll be in Canada."),
           ("complete", "completar", "The work will have been completed."),
           ("graduate", "graduarse", "I'll have graduated by then."),
           ("retire", "jubilarse", "She'll have retired by 2040."),
           ("goal", "meta", "I'll have reached my goal."),
           ("reach", "alcanzar / llegar", "We'll reach the city at noon."),
           ("save (money)", "ahorrar", "I'll have saved enough by June."),
           ("progress", "progreso", "You'll have made a lot of progress."),
           ("expect", "esperar (que pase)", "I expect we'll be finished by six."),
           ("schedule", "programar / horario", "The launch is scheduled for May.")],
          [C("At 10 p.m. tomorrow, I ___ a plane.", ["will take", "will be taking", "will have taken"], 1),
           C("By next June, I ___ my degree.", ["will finish", "will have finished", "will be finishing"], 1),
           C("___ you be using the car this evening?", ["Will", "Are", "Do"], 0),
           C("By the time we arrive, the movie ___.", ["will start", "will have started", "starts"], 1),
           C("Don't call me at noon. I ___ lunch.", ["will be having", "will have had", "have"], 0),
           F("By 2030, I ___ (save) enough money to buy a house.", "will have saved|'ll have saved"),
           F("This time next week, we ___ (lie) on the beach.", "will be lying|'ll be lying"),
           T("Para 2028 ya me habré graduado.", "By 2028, I'll have graduated|By 2028, I will have graduated|By 2028 I'll have graduated"),
           T("Mañana a esta hora estaré viajando.", "This time tomorrow, I'll be traveling|This time tomorrow I'll be traveling|This time tomorrow, I will be traveling|This time tomorrow, I'll be travelling")]),

        U("b2-07", "You should have told me", "Past modals",
          "Criticize, speculate and talk about past possibilities.",
          [("Criticism and regret", """
            - **should have / ought to have + p.p.** → it was a good idea but you didn't do it → *You **should have told** me.*
            - **shouldn't have + p.p.** → you did it but it was a bad idea → *I **shouldn't have eaten** so much.*
            """),
           ("Deduction about the past", """
            - **must have + p.p.** → I'm sure it happened → *She **must have forgotten**.*
            - **might / may / could have + p.p.** → maybe it happened → *He **might have missed** the bus.*
            - **can't / couldn't have + p.p.** → I'm sure it didn't happen → *They **can't have finished** already.*
            """),
           ("Past possibility", """
            - **could have + p.p.** → it was possible but didn't happen → *You **could have died**!*
            - **needn't have + p.p.** / **didn't need to** → *You **needn't have brought** a gift.* (you did, but it wasn't necessary)
            """)],
          [("You should have called me.", "Debiste haberme llamado."),
           ("I shouldn't have said that.", "No debí haber dicho eso."),
           ("She must have forgotten.", "Se le debe haber olvidado."),
           ("He might have missed the bus.", "Puede que haya perdido el bus."),
           ("They can't have finished already.", "No pueden haber terminado ya."),
           ("You could have been hurt!", "¡Te pudiste haber lastimado!"),
           ("We should have left earlier.", "Debimos haber salido antes."),
           ("You needn't have bought anything.", "No hacía falta que compraras nada.")],
          [("should have", "debería haber", "I should have studied."),
           ("must have", "debe haber (seguro)", "It must have been hard."),
           ("might have", "puede que haya", "She might have left."),
           ("could have", "podría haber", "We could have won."),
           ("can't have", "no puede haber", "He can't have seen us."),
           ("hurt", "lastimar(se)", "Did you get hurt?"),
           ("earlier", "más temprano", "We should have left earlier."),
           ("warn", "advertir", "You should have warned me."),
           ("notice", "notar", "I must not have noticed."),
           ("careless", "descuidado", "That was careless."),
           ("drop", "dejar caer / se me cayó", "I must have dropped it."),
           ("misunderstand", "malinterpretar", "I must have misunderstood.")],
          [C("You failed? You ___ more.", ["should study", "should have studied", "must have studied"], 1),
           C("The lights are off. They ___ to bed.", ["must have gone", "should have gone", "can't have gone"], 0),
           C("I'm not sure where my phone is. I ___ it at the office.", ["must have left", "might have left", "should have left"], 1),
           C("He ___ the email; he was offline all day.", ["can't have read", "must have read", "should have read"], 0),
           C("You drove at 150 km/h? You ___ killed!", ["could have been", "must have been", "should have been"], 0),
           F("I ___ (not / eat) so much. I feel sick.", "shouldn't have eaten|should not have eaten"),
           F("She's late. She must ___ (miss) the bus.", "have missed"),
           T("Debiste haberme llamado.", "You should have called me|You should've called me"),
           T("No debí haber dicho eso.", "I shouldn't have said that|I should not have said that")]),

        U("b2-08", "However, although, despite", "Linking words and contrast",
          "Connect ideas like a fluent speaker and writer.",
          [("Contrast", """
            - **although / even though** + clause → *Although it was raining, we went out.*
            - **despite / in spite of** + noun or **-ing** → *Despite the rain, we went out. / Despite **being** tired, she kept working.*
            - **however** → starts a new sentence, followed by a comma → *It was expensive. **However**, it was worth it.*
            - **whereas / while** → compares two facts → *I love coffee, **whereas** my sister prefers tea.*

            ❌ *Despite it was raining* → ✅ *Despite the rain* / *Despite the fact that it was raining*
            """),
           ("Cause, result, addition", """
            - Cause: **because** + clause, **because of / due to** + noun
            - Result: **so**, **therefore**, **as a result**
            - Addition: **moreover**, **furthermore**, **in addition**, **besides**
            - Purpose: **so that**, **in order to**

            > The flight was canceled **due to** bad weather. **As a result**, we stayed another night.
            """)],
          [("Although it was late, we kept working.", "Aunque era tarde, seguimos trabajando."),
           ("Despite the traffic, we arrived on time.", "A pesar del tráfico, llegamos a tiempo."),
           ("It's expensive. However, it's worth it.", "Es caro. Sin embargo, vale la pena."),
           ("I like coffee, whereas my sister prefers tea.", "A mí me gusta el café, mientras que mi hermana prefiere té."),
           ("The flight was canceled due to the storm.", "El vuelo se canceló debido a la tormenta."),
           ("He studied hard; therefore, he passed.", "Estudió mucho; por lo tanto, aprobó."),
           ("In spite of being tired, she went to the gym.", "A pesar de estar cansada, fue al gimnasio."),
           ("I saved money so that I could travel.", "Ahorré dinero para poder viajar.")],
          [("although", "aunque", "Although I was tired, I went."),
           ("even though", "aun cuando / aunque", "Even though it's hard, I like it."),
           ("despite", "a pesar de", "Despite the cost, we bought it."),
           ("however", "sin embargo", "However, there's a problem."),
           ("whereas", "mientras que", "He's tall, whereas I'm short."),
           ("therefore", "por lo tanto", "Therefore, we must act."),
           ("due to", "debido a", "Due to the rain, the game was canceled."),
           ("moreover", "además", "Moreover, it's cheap."),
           ("as a result", "como resultado", "As a result, sales increased."),
           ("so that", "para que", "Speak slowly so that they understand."),
           ("worth it", "vale la pena", "It was totally worth it."),
           ("on the other hand", "por otro lado", "On the other hand, it's risky.")],
          [C("___ it was raining, we went to the beach.", ["Despite", "Although", "However"], 1),
           C("___ the rain, we went to the beach.", ["Although", "Despite", "Even though"], 1),
           C("It's a great phone. ___, the battery is bad.", ["Although", "However", "Despite"], 1),
           C("The game was canceled ___ the storm.", ["because", "due to", "although"], 1),
           C("Correct:", ["Despite it was late, we stayed.", "Despite being late, we stayed.", "Despite of being late, we stayed."], 1),
           C("I'm a morning person, ___ my brother loves the night.", ["whereas", "therefore", "despite"], 0),
           F("In spite ___ the problems, the project was a success.", "of"),
           F("I'm saving money so ___ I can travel.", "that"),
           T("Aunque era tarde, seguimos trabajando.", "Although it was late, we kept working|Even though it was late, we kept working|Although it was late, we continued working|Although it was late we kept working"),
           T("A pesar del tráfico, llegamos a tiempo.", "Despite the traffic, we arrived on time|In spite of the traffic, we arrived on time|Despite the traffic, we got there on time")]),

        U("b2-09", "Which, by the way, …", "Non-defining relative clauses and participle clauses",
          "Add extra information elegantly and make sentences more concise.",
          [("Non-defining relative clauses", """
            They add **extra** information. Use **commas**, and **never** use *that*:

            > My brother, **who lives in Colombia**, is an engineer.
            > Antigua, **which** was the capital, is a UNESCO site.

            **which** can refer to the whole previous idea:
            > He didn't call, **which** was strange.
            """),
           ("Participle clauses", """
            Shorter, more advanced way to combine sentences:

            - **-ing** (active): *The man **sitting** next to me was snoring.* (who was sitting)
            - **past participle** (passive): *The app, **developed** in Guatemala, has 1M users.*
            - Time/reason: ***Having finished** the report, I went home.* (After I had finished…)
            - ***Not knowing** what to do, she called me.*
            """)],
          [("My sister, who lives in Spain, is a doctor.", "Mi hermana, que vive en España, es doctora."),
           ("Antigua, which was the old capital, is very beautiful.", "Antigua, que fue la antigua capital, es muy bonita."),
           ("He didn't reply, which was strange.", "No respondió, lo cual fue extraño."),
           ("The girl sitting next to me is my cousin.", "La chica sentada a mi lado es mi prima."),
           ("Having finished the report, I went home.", "Después de terminar el informe, me fui a casa."),
           ("The house, built in 1800, is a museum now.", "La casa, construida en 1800, ahora es un museo."),
           ("Not knowing what to say, I stayed quiet.", "Sin saber qué decir, me quedé callado."),
           ("Our teacher, whose classes are great, is leaving.", "Nuestro profesor, cuyas clases son geniales, se va.")],
          [("which (whole idea)", "lo cual", "She passed, which surprised everyone."),
           ("extra information", "información adicional", "Commas mark extra information."),
           ("reply", "responder", "He didn't reply to my message."),
           ("strange", "extraño", "That's strange."),
           ("snore", "roncar", "He snores loudly."),
           ("located", "ubicado", "The office is located downtown."),
           ("founded", "fundado", "The company, founded in 2010, …"),
           ("surprisingly", "sorprendentemente", "Surprisingly, it worked."),
           ("quiet", "callado / tranquilo", "Keep quiet."),
           ("former", "anterior / ex-", "The former capital."),
           ("heritage", "patrimonio", "World Heritage Site."),
           ("concise", "conciso", "Be concise.")],
          [C("My dad, ___ is 60, still works.", ["that", "who", "which"], 1),
           C("He failed the exam, ___ surprised everyone.", ["what", "that", "which"], 2),
           C("Which is correct?", ["My car, that is red, is fast.", "My car, which is red, is fast.", "My car which is red, is fast."], 1),
           C("The woman ___ at the door is my aunt.", ["stands", "standing", "stood"], 1),
           C("___ the project, we celebrated.", ["Finishing", "Having finished", "Finished"], 1),
           C("The book, ___ in 1950, is still popular.", ["writing", "written", "wrote"], 1),
           F("I met Ana, ___ brother works with me.", "whose"),
           F("___ (not / know) the way, we used the GPS.", "Not knowing"),
           T("Mi hermano, que vive en Colombia, es ingeniero.", "My brother, who lives in Colombia, is an engineer"),
           T("No respondió, lo cual fue extraño.", "He didn't reply, which was strange|She didn't reply, which was strange|He didn't answer, which was strange")]),

        U("b2-10", "Make or do?", "Collocations, phrasal verbs and word formation",
          "Sound natural with common word combinations and build new words.",
          [("Make vs do", """
            - **make**: create or produce → make a mistake, make a decision, make money, make friends, make progress, make a phone call
            - **do**: tasks and activities → do homework, do the dishes, do business, do a favor, do your best, do research

            🇪🇸 *Hacer* is both, so you have to learn the collocations.
            """),
           ("Common phrasal verbs", """
            - **figure out** = understand / solve · **come up with** = think of (an idea)
            - **run out of** = have no more · **put off** = postpone
            - **get along with** = have a good relationship · **look into** = investigate
            - **turn down** = reject · **carry on** = continue · **give up** = stop trying

            > We **ran out of** time, so we **put off** the meeting.
            """),
           ("Word formation", """
            - Nouns: -tion (decide → decision), -ment (develop → development), -ness (happy → happiness), -ity (possible → possibility)
            - Adjectives: -ful (use → useful), -less (use → useless), -able (afford → affordable), -ive (create → creative)
            - Negative prefixes: **un**likely, **im**possible, **in**credible, **dis**agree, **ir**responsible
            """)],
          [("I made a big mistake.", "Cometí un gran error."),
           ("Can you do me a favor?", "¿Me haces un favor?"),
           ("We ran out of coffee.", "Se nos acabó el café."),
           ("I can't figure out this bug.", "No logro entender este bug."),
           ("She came up with a great idea.", "Se le ocurrió una gran idea."),
           ("Don't give up!", "¡No te rindas!"),
           ("They turned down my offer.", "Rechazaron mi oferta."),
           ("I get along well with my coworkers.", "Me llevo bien con mis compañeros de trabajo.")],
          [("make a decision", "tomar una decisión", "I need to make a decision."),
           ("do research", "investigar", "I did some research."),
           ("figure out", "descifrar / resolver", "I figured it out!"),
           ("come up with", "ocurrírsele (idea)", "We came up with a plan."),
           ("run out of", "quedarse sin", "We ran out of gas."),
           ("put off", "posponer", "Stop putting it off."),
           ("get along with", "llevarse bien con", "I get along with my sister."),
           ("look into", "investigar / revisar", "I'll look into it."),
           ("turn down", "rechazar", "She turned down the job."),
           ("give up", "rendirse / dejar", "Never give up."),
           ("carry on", "continuar", "Carry on, please."),
           ("affordable", "asequible", "It's an affordable phone.")],
          [C("I ___ a mistake in the code.", ["did", "made", "had"], 1),
           C("Can you ___ me a favor?", ["make", "do", "give"], 1),
           C("We've run ___ of milk.", ["out", "off", "up"], 0),
           C("Rechazaron mi propuesta:", ["They turned down my proposal.", "They turned off my proposal.", "They gave up my proposal."], 0),
           C("Negative of 'possible':", ["unpossible", "impossible", "dispossible"], 1),
           C("Noun of 'decide':", ["decidement", "decision", "decidness"], 1),
           F("Don't ___ up! You're almost there.", "give"),
           F("She came up ___ a brilliant solution.", "with"),
           F("I can't figure ___ how this works.", "out"),
           T("Se nos acabó el tiempo.", "We ran out of time"),
           T("Me llevo bien con mi jefe.", "I get along well with my boss|I get along with my boss|I get on well with my boss")]),
    ],
}
