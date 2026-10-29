from helpers import U, C, F, T, O

LEVEL = {
    "id": "B1",
    "title": "B1 · Intermedio",
    "description": "Te defiendes en la mayoría de situaciones: cuentas experiencias, opinas y planteas hipótesis.",
    "units": [
        U("b1-01", "Since and for", "Present perfect vs past simple",
          "Elegir bien entre present perfect y past simple, y usar for / since.",
          [("Unfinished time vs finished time", """
            - **Past simple**: acción terminada en un momento **terminado** (yesterday, in 2020, last week, ago). *I lived in Xela in 2019.*
            - **Present perfect**: conecta el pasado con **ahora** (resultado actual, experiencia, periodo que no ha terminado). *I've lived here for five years.* (todavía vivo aquí)

            > I **lost** my keys yesterday. (historia terminada)
            > I**'ve lost** my keys! (y ahora no las tengo)
            """),
           ("For vs since", """
            - **for** + duración → for two years, for a long time, for ages
            - **since** + punto de inicio → since 2023, since Monday, since I was a child

            > I've worked at BYTE **since** 2023. → I've worked there **for** almost three years.
            > **How long have you** known her? (no *How long do you know her?*)

            Error típico de hispanohablantes: *I live here since 2010* ❌ → **I've lived here since 2010** ✅
            """),
           ("This week / today / this year", """
            Si el periodo **no ha terminado**, se usa present perfect: *I've had three coffees today.*
            Si ya terminó: *I had three coffees yesterday.*
            """)],
          [("I've worked here since 2023.", "Trabajo aquí desde 2023."),
           ("How long have you known him?", "¿Cuánto tiempo hace que lo conoces?"),
           ("We've been friends for ten years.", "Somos amigos desde hace diez años."),
           ("I lost my wallet last week.", "Perdí mi billetera la semana pasada."),
           ("She's just got a new job.", "Ella acaba de conseguir un trabajo nuevo."),
           ("I haven't seen him since Monday.", "No lo he visto desde el lunes."),
           ("Did you see the game last night?", "¿Viste el partido anoche?"),
           ("I've had three coffees today.", "Me he tomado tres cafés hoy.")],
          [("since", "desde (punto)", "I've been here since nine."),
           ("for", "durante / desde hace", "I've studied for two hours."),
           ("how long", "cuánto tiempo", "How long have you lived here?"),
           ("lately", "últimamente", "Have you seen her lately?"),
           ("recently", "recientemente", "I've recently moved."),
           ("so far", "hasta ahora", "So far, so good."),
           ("ages", "muchísimo tiempo", "I haven't seen you for ages!"),
           ("move (house)", "mudarse", "We moved to Mixco in 2022."),
           ("get a job", "conseguir trabajo", "She got a job at a bank."),
           ("graduate", "graduarse", "I'll graduate in 2028."),
           ("career", "carrera profesional", "She has a great career."),
           ("achieve", "lograr", "We've achieved a lot this year.")],
          [C("I ___ in Guatemala City since 2022.", ["live", "have lived", "lived"], 1),
           C("She ___ to Spain in 2019.", ["has gone", "went", "has been"], 1),
           C("We've known each other ___ we were kids.", ["for", "since", "from"], 1),
           C("I haven't seen her ___ ages.", ["since", "for", "during"], 1),
           C("___ have you worked there?", ["How much", "How long", "Since when you"], 1),
           C("Elige la correcta:", ["I know him since 2015.", "I've known him since 2015.", "I knew him since 2015."], 1),
           F("I ___ (not / see) him since Monday.", "haven't seen|have not seen"),
           F("They ___ (get) married in 2018.", "got"),
           F("We've lived here ___ five years.", "for"),
           T("Trabajo aquí desde 2023.", "I've worked here since 2023|I have worked here since 2023|I've been working here since 2023"),
           T("¿Cuánto tiempo hace que la conoces?", "How long have you known her?")]),

        U("b1-02", "I've been waiting", "Present perfect continuous",
          "Hablar de actividades que empezaron en el pasado y siguen (o acaban de terminar).",
          [("Form", """
            **have / has been + verb-ing**

            > I've been studying English for three years.
            > She's been working all day.
            > How long **have you been waiting**?
            """),
           ("Simple vs continuous", """
            - **Continuous**: énfasis en la **actividad** y su duración. *I've been painting the house.* (tal vez no terminé)
            - **Simple**: énfasis en el **resultado** o la cantidad. *I've painted two rooms.* (terminadas)

            También usamos el continuo para explicar **evidencia** de algo reciente:
            > Your eyes are red. Have you been crying?

            Recuerda: verbos de estado (know, like, want, be) normalmente no van en continuo → *I've known her for years.*
            """)],
          [("I've been studying for three hours.", "Llevo tres horas estudiando."),
           ("How long have you been waiting?", "¿Cuánto tiempo llevas esperando?"),
           ("It's been raining all morning.", "Ha estado lloviendo toda la mañana."),
           ("She's been working here since May.", "Ella trabaja aquí desde mayo."),
           ("You look tired. Have you been running?", "Te ves cansado. ¿Estuviste corriendo?"),
           ("I've read three chapters.", "He leído tres capítulos."),
           ("We've been talking about you.", "Hemos estado hablando de ti."),
           ("He's been playing video games all day.", "Ha estado jugando videojuegos todo el día.")],
          [("all day", "todo el día", "I've been coding all day."),
           ("lately", "últimamente", "I've been sleeping badly lately."),
           ("wait for", "esperar a", "I've been waiting for you."),
           ("look for", "buscar", "I've been looking for a new job."),
           ("practice", "practicar", "I've been practicing every day."),
           ("improve", "mejorar", "Your English has improved a lot."),
           ("tired", "cansado", "I'm tired because I've been working."),
           ("exhausted", "agotado", "I'm exhausted."),
           ("paint", "pintar", "We've been painting the kitchen."),
           ("cry", "llorar", "Have you been crying?"),
           ("chapter", "capítulo", "I've read two chapters."),
           ("on and off", "de forma intermitente", "It's been raining on and off.")],
          [C("I ___ for you for an hour!", ["wait", "am waiting", "have been waiting"], 2),
           C("She ___ three books this month.", ["has been reading", "has read", "read"], 1, "Cantidad/resultado → simple."),
           C("Your hands are dirty. ___ in the garden?", ["Have you worked", "Have you been working", "Did you work"], 1),
           C("Elige la correcta:", ["I've been knowing her for years.", "I've known her for years.", "I know her for years."], 1),
           C("How long ___ learning English?", ["have you been", "are you", "do you"], 0),
           F("It ___ (rain) since this morning.", "has been raining|'s been raining"),
           F("They've ___ talking for hours.", "been"),
           T("Llevo tres horas estudiando.", "I've been studying for three hours|I have been studying for three hours"),
           T("¿Cuánto tiempo llevas esperando?", "How long have you been waiting?")]),

        U("b1-03", "If it rains…", "First conditional y time clauses",
          "Hablar de situaciones posibles en el futuro y sus consecuencias.",
          [("First conditional", """
            **If + present simple, will + verbo** → situaciones **reales o probables**:

            > If it rains, we'll stay at home.
            > If you don't study, you won't pass.
            > What will you do if they offer you the job?

            ⚠️ Después de **if** NO se usa will: *If it will rain* ❌ → **If it rains** ✅
            """),
           ("Unless, when, as soon as", """
            Las mismas reglas aplican con:
            - **unless** = if… not → *I won't go unless you come.* (no iré a menos que vengas)
            - **when** → *I'll call you when I arrive.* (cuando llegue — en español subjuntivo, en inglés presente)
            - **as soon as** (en cuanto), **before**, **after**, **until**

            > I'll send it as soon as I finish.
            """)],
          [("If it rains, we'll stay home.", "Si llueve, nos quedamos en casa."),
           ("I'll call you when I get home.", "Te llamo cuando llegue a casa."),
           ("If you don't hurry, you'll miss the bus.", "Si no te apuras, vas a perder el bus."),
           ("I won't go unless you come with me.", "No iré a menos que vengas conmigo."),
           ("As soon as I finish, I'll send it.", "En cuanto termine, lo envío."),
           ("What will you do if you fail?", "¿Qué harás si repruebas?"),
           ("If I have time, I'll help you.", "Si tengo tiempo, te ayudo."),
           ("We'll wait until you arrive.", "Esperaremos hasta que llegues.")],
          [("unless", "a menos que", "Unless you hurry, we'll be late."),
           ("as soon as", "en cuanto", "Call me as soon as you know."),
           ("until", "hasta que", "Wait until I come back."),
           ("miss", "perder (bus) / extrañar", "Hurry or you'll miss the bus."),
           ("hurry", "apurarse", "Hurry up!"),
           ("pass (an exam)", "aprobar", "If you study, you'll pass."),
           ("fail", "reprobar / fallar", "I hope I don't fail."),
           ("offer", "ofrecer / oferta", "They offered me a job."),
           ("accept", "aceptar", "Will you accept the offer?"),
           ("get home", "llegar a casa", "Text me when you get home."),
           ("in case", "por si acaso", "Take an umbrella in case it rains."),
           ("deal", "trato", "Deal! See you tomorrow.")],
          [C("If it ___ tomorrow, we'll cancel the trip.", ["will rain", "rains", "rained"], 1),
           C("I'll call you when I ___.", ["will arrive", "arrive", "arrived"], 1),
           C("If you don't study, you ___ pass.", ["don't", "won't", "didn't"], 1),
           C("I won't go ___ you come with me.", ["if", "unless", "when"], 1),
           C("___ I finish, I'll send you the file.", ["As soon as", "Unless", "Until"], 0),
           F("If she ___ (call), I'll tell her.", "calls"),
           F("We'll wait ___ you arrive. (hasta que)", "until"),
           F("What ___ you do if you lose your job?", "will"),
           T("Si llueve, nos quedamos en casa.", "If it rains, we'll stay home|If it rains, we will stay home|If it rains, we'll stay at home|If it rains we'll stay home"),
           T("Te llamo cuando llegue.", "I'll call you when I arrive|I'll call you when I get there|I will call you when I arrive|I'll call you when I get home")]),

        U("b1-04", "If I were you", "Second conditional",
          "Imaginar situaciones irreales o poco probables y dar consejos.",
          [("Form", """
            **If + past simple, would + verbo** → situaciones **imaginarias**, poco probables o irreales en el presente/futuro:

            > If I had more time, I would learn French.
            > If I won the lottery, I'd buy a house for my parents.
            > What would you do if you lost your phone?

            El pasado aquí **no** habla del pasado: habla de algo hipotético.
            """),
           ("If I were you…", """
            Con to be se usa **were** para todas las personas (especialmente en inglés formal):

            > If I **were** you, I'd accept the offer. (yo que tú…)
            > If she **were** here, she'd know what to do.

            En conversación también se escucha *If I was…*, pero **If I were you** es la expresión fija para dar consejos.
            """),
           ("1st vs 2nd conditional", """
            - *If I **get** the job, I**'ll** move.* → es posible, lo veo real.
            - *If I **got** the job, I**'d** move.* → lo veo poco probable o lo estoy imaginando.
            """)],
          [("If I had more time, I'd travel more.", "Si tuviera más tiempo, viajaría más."),
           ("If I were you, I'd take the job.", "Yo que tú, aceptaría el trabajo."),
           ("What would you do if you won the lottery?", "¿Qué harías si ganaras la lotería?"),
           ("I wouldn't do that if I were you.", "Yo que tú no haría eso."),
           ("If she knew the answer, she would tell us.", "Si ella supiera la respuesta, nos la diría."),
           ("If we lived near the beach, we'd go every weekend.", "Si viviéramos cerca de la playa, iríamos cada fin de semana."),
           ("Would you move abroad if you could?", "¿Te irías a vivir al extranjero si pudieras?"),
           ("It would be great if you came.", "Sería genial que vinieras.")],
          [("would", "-ría (condicional)", "I would love to go."),
           ("lottery", "lotería", "If I won the lottery…"),
           ("abroad", "al/en el extranjero", "I'd like to study abroad."),
           ("afford", "poder pagar algo", "I can't afford a new car."),
           ("rich", "rico", "If I were rich, I'd travel."),
           ("imagine", "imaginar", "Imagine you could fly."),
           ("choose", "elegir", "Which one would you choose?"),
           ("instead", "en cambio / en vez de eso", "I'd take the bus instead."),
           ("situation", "situación", "What would you do in that situation?"),
           ("honest", "honesto", "To be honest, I wouldn't go."),
           ("could", "podría", "If I could, I would help."),
           ("dream", "sueño / soñar", "It's my dream job.")],
          [C("If I ___ more money, I'd buy a new laptop.", ["have", "had", "would have"], 1),
           C("If I were you, I ___ talk to her.", ["will", "would", "am"], 1),
           C("If she ___ here, she would help us.", ["is", "were", "will be"], 1),
           C("What ___ you do if you saw a ghost?", ["will", "would", "did"], 1),
           C("Lo ves poco probable:", ["If I get the job, I'll move.", "If I got the job, I'd move.", "If I get the job, I'd move."], 1),
           C("Elige la correcta:", ["If I would have time, I'd go.", "If I had time, I'd go.", "If I have time, I'd go."], 1),
           F("If I ___ (can) fly, I would go everywhere.", "could"),
           F("If I ___ you, I'd study more.", "were|was"),
           T("Yo que tú, aceptaría el trabajo.", "If I were you, I'd take the job|If I were you, I would take the job|If I were you, I'd accept the job|If I were you I'd take the job"),
           T("¿Qué harías si ganaras la lotería?", "What would you do if you won the lottery?")]),

        U("b1-05", "It was built in…", "Voz pasiva (presente y pasado)",
          "Hablar de procesos y hechos cuando lo importante no es quién lo hizo.",
          [("Form", """
            **be + participio pasado**

            - Presente: *English **is spoken** all over the world.*
            - Pasado: *Tikal **was built** by the Maya.*
            - Con agente (opcional): **by** + persona → *The app was designed **by** a student.*

            Negativo y preguntas: *It wasn't made in China. Where was it made?*
            """),
           ("When do we use it?", """
            - Cuando **no sabemos** o **no importa** quién hizo la acción: *My phone was stolen.*
            - Para **procesos** y textos formales: *The data is stored in the cloud.*
            - Para enfocar el objeto: *This song was written in 1985.*

            En español a veces usamos "se": *Se habla inglés* → **English is spoken**.
            """)],
          [("English is spoken all over the world.", "El inglés se habla en todo el mundo."),
           ("Tikal was built by the Maya.", "Tikal fue construido por los mayas."),
           ("My phone was stolen.", "Me robaron el teléfono."),
           ("The meeting was canceled.", "La reunión fue cancelada."),
           ("Coffee is grown in the highlands.", "El café se cultiva en el altiplano."),
           ("When was this house built?", "¿Cuándo se construyó esta casa?"),
           ("The data is stored on the server.", "Los datos se guardan en el servidor."),
           ("I was born in Antigua.", "Nací en Antigua.")],
          [("build (built)", "construir", "The church was built in 1700."),
           ("steal (stole, stolen)", "robar (objeto)", "My bike was stolen."),
           ("make (made)", "hacer / fabricar", "It's made in Guatemala."),
           ("grow (grown)", "cultivar / crecer", "Coffee is grown here."),
           ("invent", "inventar", "The telephone was invented in 1876."),
           ("design", "diseñar", "The app was designed by me."),
           ("store", "almacenar", "Files are stored in the cloud."),
           ("cancel", "cancelar", "The flight was canceled."),
           ("hire", "contratar", "She was hired last month."),
           ("fix", "arreglar", "My car was fixed yesterday."),
           ("send (sent)", "enviar", "The email was sent at 9."),
           ("destroy", "destruir", "The city was destroyed by an earthquake.")],
          [C("This bridge ___ in 1990.", ["built", "was built", "is build"], 1),
           C("Spanish ___ in many countries.", ["speaks", "is spoken", "is speaking"], 1),
           C("The Mona Lisa was painted ___ Leonardo da Vinci.", ["for", "from", "by"], 2),
           C("Me robaron la billetera:", ["I stole my wallet.", "My wallet was stolen.", "My wallet stole."], 1),
           C("Where ___ these shoes made?", ["was", "were", "did"], 1),
           F("The results were ___ (publish) yesterday.", "published"),
           F("This app ___ (use) by millions of people. (presente)", "is used"),
           F("The flight ___ (cancel) because of the storm. (pasado)", "was canceled|was cancelled"),
           T("El café se cultiva en Guatemala.", "Coffee is grown in Guatemala"),
           T("¿Cuándo se construyó?", "When was it built?")]),

        U("b1-06", "The person who…", "Relative clauses (defining)",
          "Unir ideas y describir personas, cosas y lugares con who, which, that y where.",
          [("Relative pronouns", """
            - **who** → personas: *The woman **who** lives next door is a doctor.*
            - **which** → cosas: *The laptop **which** I bought is fast.*
            - **that** → personas o cosas (muy común en conversación)
            - **where** → lugares: *That's the café **where** we met.*
            - **whose** → posesión: *He's the guy **whose** car was stolen.*
            """),
           ("Omitting the pronoun", """
            Si el pronombre es el **objeto** del verbo, se puede omitir:

            > The movie (that) **we watched** was great. ✅
            > The man **who called** you is here. → aquí *who* es sujeto, no se omite.

            ⚠️ No repitas el pronombre: *The book that I read **it*** ❌ → *The book that I read* ✅
            """)],
          [("The woman who lives next door is a nurse.", "La mujer que vive al lado es enfermera."),
           ("This is the phone that I bought.", "Este es el teléfono que compré."),
           ("That's the restaurant where we met.", "Ese es el restaurante donde nos conocimos."),
           ("He's the guy whose car was stolen.", "Él es el chavo al que le robaron el carro."),
           ("The movie we watched was boring.", "La película que vimos estuvo aburrida."),
           ("I need someone who speaks English.", "Necesito a alguien que hable inglés."),
           ("The app which I use the most is Spotify.", "La app que más uso es Spotify."),
           ("Do you know anyone who can help?", "¿Conoces a alguien que pueda ayudar?")],
          [("who", "que / quien (personas)", "The man who called is my uncle."),
           ("which", "que / el cual (cosas)", "The car which I drive is old."),
           ("where", "donde", "The town where I grew up is small."),
           ("whose", "cuyo / de quien", "The girl whose dog is lost is crying."),
           ("someone", "alguien", "Someone is at the door."),
           ("anyone", "alguien (preguntas) / cualquiera", "Is anyone home?"),
           ("neighbor", "vecino", "My neighbor is very friendly."),
           ("grow up", "crecer (persona)", "I grew up in Ciudad Vieja."),
           ("colleague", "colega", "A colleague helped me."),
           ("customer", "cliente", "The customer who complained left."),
           ("guy", "chavo / tipo", "He's a nice guy."),
           ("place", "lugar", "It's the place where I work.")],
          [C("The man ___ fixed my car is very good.", ["which", "who", "where"], 1),
           C("This is the house ___ I was born.", ["which", "where", "who"], 1),
           C("The book ___ you lent me was amazing.", ["who", "that", "where"], 1),
           C("She's the woman ___ son is my classmate.", ["who", "whose", "which"], 1),
           C("Elige la correcta:", ["The pizza that I ate it was cold.", "The pizza that I ate was cold.", "The pizza who I ate was cold."], 1),
           C("¿En cuál se puede omitir 'that'?", ["The bus that goes to Antigua", "The song that I like", "The dog that bit me"], 1,
             "Solo cuando el pronombre es **objeto**: *the song I like*."),
           F("I have a friend ___ lives in Canada.", "who|that"),
           F("That's the office ___ I work.", "where"),
           T("Necesito a alguien que hable inglés.", "I need someone who speaks English|I need someone that speaks English|I need somebody who speaks English"),
           T("Ese es el lugar donde nos conocimos.", "That's the place where we met|That is the place where we met")]),

        U("b1-07", "She said that…", "Reported speech (statements)",
          "Contar lo que otra persona dijo.",
          [("Backshift", """
            Cuando reportamos con **said** o **told**, el tiempo verbal normalmente "retrocede":

            - present → past: "I **am** tired." → She said she **was** tired.
            - present continuous → past continuous
            - past / present perfect → past perfect: "I **saw** it." → He said he **had seen** it.
            - will → **would**; can → **could**

            También cambian pronombres y referencias: *now → then*, *today → that day*, *tomorrow → the next day*, *here → there*.
            """),
           ("Say vs tell", """
            - **say** (something): *She **said** (that) she was busy.*
            - **tell** + persona: *She **told me** (that) she was busy.*

            ❌ *She said me* → ✅ *She told me* / *She said to me*
            """)],
          [("She said she was tired.", "Ella dijo que estaba cansada."),
           ("He told me he would call.", "Me dijo que llamaría."),
           ("They said they had finished.", "Dijeron que habían terminado."),
           ("My boss told us the meeting was canceled.", "Mi jefe nos dijo que la reunión se canceló."),
           ("She said she couldn't come.", "Dijo que no podía venir."),
           ("He said he was working from home.", "Dijo que estaba trabajando desde casa."),
           ("I told her I loved the gift.", "Le dije que me encantó el regalo."),
           ("You said it was easy!", "¡Dijiste que era fácil!")],
          [("say (said)", "decir", "What did she say?"),
           ("tell (told)", "decir (a alguien) / contar", "Tell me the truth."),
           ("explain", "explicar", "He explained that it was late."),
           ("mention", "mencionar", "She mentioned she was moving."),
           ("promise", "prometer", "He promised he would help."),
           ("complain", "quejarse", "They complained that it was cold."),
           ("answer", "responder", "She answered that she didn't know."),
           ("admit", "admitir", "He admitted he was wrong."),
           ("boss", "jefe", "My boss said I could leave early."),
           ("truth", "verdad", "Tell me the truth."),
           ("lie", "mentira / mentir", "He told me a lie."),
           ("rumor", "rumor", "I heard a rumor.")],
          [C("\"I'm busy.\" → She said she ___ busy.", ["is", "was", "were"], 1),
           C("\"I will help you.\" → He said he ___ help me.", ["will", "would", "can"], 1),
           C("\"I saw the movie.\" → She said she ___ the movie.", ["saw", "had seen", "has seen"], 1),
           C("She ___ me she was leaving.", ["said", "told", "say"], 1),
           C("He ___ that he was hungry.", ["told", "said", "told me to"], 1),
           C("\"I can swim.\" → He said he ___ swim.", ["can", "could", "would"], 1),
           F("\"We're working.\" → They said they ___ working.", "were"),
           F("She told ___ that she was sorry. (a mí)", "me"),
           T("Ella dijo que estaba cansada.", "She said she was tired|She said that she was tired"),
           T("Me dijo que llamaría.", "He told me he would call|He told me that he would call|She told me she would call|He told me he'd call")]),

        U("b1-08", "I enjoy learning", "Gerunds and infinitives",
          "Saber cuándo usar verbo-ing y cuándo to + verbo.",
          [("Verb + -ing", """
            Después de algunos verbos va **-ing**:

            **enjoy, mind, finish, avoid, suggest, keep, practice, miss, consider, can't stand**

            > I enjoy **reading**. Do you mind **waiting**? I've finished **working**.

            También después de **preposiciones**: *I'm good **at** cooking. Thanks **for** helping. I'm interested **in** learning.*
            Y como sujeto: ***Learning** English is fun.*
            """),
           ("Verb + to-infinitive", """
            Después de otros va **to + verbo**:

            **want, need, decide, hope, plan, learn, promise, agree, refuse, would like, afford**

            > I want **to travel**. She decided **to quit**. I'd like **to order**, please.
            """),
           ("Change of meaning", """
            - **stop -ing** = dejar de · **stop to** = detenerse para → *I stopped smoking.* / *I stopped to buy water.*
            - **remember -ing** = recordar algo pasado · **remember to** = acordarse de hacer → *Remember to call her!*
            - **try -ing** = probar · **try to** = intentar
            """)],
          [("I enjoy learning languages.", "Disfruto aprender idiomas."),
           ("She decided to quit her job.", "Ella decidió renunciar a su trabajo."),
           ("Do you mind closing the window?", "¿Te importaría cerrar la ventana?"),
           ("I'm good at solving problems.", "Soy bueno resolviendo problemas."),
           ("We need to talk.", "Necesitamos hablar."),
           ("I stopped drinking soda.", "Dejé de tomar gaseosa."),
           ("Remember to lock the door.", "Acuérdate de cerrar la puerta con llave."),
           ("Thanks for helping me.", "Gracias por ayudarme.")],
          [("enjoy", "disfrutar", "I enjoy coding."),
           ("avoid", "evitar", "Avoid eating late."),
           ("mind", "importar (molestar)", "I don't mind waiting."),
           ("keep", "seguir (haciendo)", "Keep practicing!"),
           ("quit", "renunciar / dejar", "He quit his job."),
           ("refuse", "negarse", "She refused to answer."),
           ("agree", "estar de acuerdo / acceder", "They agreed to help."),
           ("suggest", "sugerir", "I suggest taking a break."),
           ("can't stand", "no soportar", "I can't stand waiting."),
           ("look forward to", "esperar con ilusión", "I look forward to hearing from you."),
           ("be used to", "estar acostumbrado a", "I'm used to working late."),
           ("lock", "cerrar con llave", "Lock the door, please.")],
          [C("I enjoy ___ video games.", ["play", "to play", "playing"], 2),
           C("She wants ___ abroad.", ["study", "to study", "studying"], 1),
           C("Thanks for ___ me.", ["help", "to help", "helping"], 2, "Después de preposición → -ing."),
           C("Dejé de fumar:", ["I stopped to smoke.", "I stopped smoking.", "I stopped smoke."], 1),
           C("Acuérdate de llamarla:", ["Remember calling her.", "Remember to call her.", "Remember call her."], 1),
           C("I look forward to ___ you.", ["see", "seeing", "to see"], 1, "En *look forward to*, **to** es preposición → -ing."),
           F("Do you mind ___ (wait) a minute?", "waiting"),
           F("We decided ___ (stay) home.", "to stay"),
           F("___ (learn) English opens many doors.", "Learning"),
           T("Disfruto aprender idiomas.", "I enjoy learning languages"),
           T("Necesitamos hablar.", "We need to talk")]),

        U("b1-09", "I used to…", "Used to, be used to, get used to",
          "Hablar de hábitos del pasado y de cosas a las que te acostumbras.",
          [("Used to + verb", """
            Hábitos o estados del pasado que **ya no** son así:

            > I **used to** play soccer every day. (ya no)
            > She **didn't use to** like coffee. (ahora sí)
            > **Did you use to** live in Xela?

            Ojo: en negativo y preguntas se escribe **use to** (sin d), porque va con did.
            """),
           ("Be used to / get used to + -ing", """
            - **be used to** + sustantivo/-ing = estar acostumbrado → *I'm used to waking up early.*
            - **get used to** + sustantivo/-ing = acostumbrarse (proceso) → *I'm getting used to the cold weather.*

            ⚠️ *I'm used to wake up* ❌ → *I'm used to **waking** up* ✅
            """)],
          [("I used to play soccer every weekend.", "Antes jugaba fútbol cada fin de semana."),
           ("She didn't use to like vegetables.", "A ella antes no le gustaban las verduras."),
           ("Did you use to live in Xela?", "¿Antes vivías en Xela?"),
           ("I'm used to working late.", "Estoy acostumbrado a trabajar tarde."),
           ("I can't get used to the cold.", "No me acostumbro al frío."),
           ("We used to have a dog.", "Antes teníamos un perro."),
           ("He's getting used to his new job.", "Se está acostumbrando a su nuevo trabajo."),
           ("There used to be a cinema here.", "Antes había un cine aquí.")],
          [("used to", "solía / antes (hábito pasado)", "I used to be shy."),
           ("be used to", "estar acostumbrado a", "I'm used to the noise."),
           ("get used to", "acostumbrarse a", "You'll get used to it."),
           ("habit", "hábito", "It's a bad habit."),
           ("childhood", "infancia", "I had a happy childhood."),
           ("shy", "tímido", "I used to be very shy."),
           ("anymore", "ya no (al final)", "I don't play anymore."),
           ("still", "todavía", "Do you still live there?"),
           ("hometown", "pueblo natal", "My hometown is Ciudad Vieja."),
           ("change", "cambiar / cambio", "A lot has changed."),
           ("routine", "rutina", "I have a new routine."),
           ("traffic", "tráfico", "I'm used to the traffic.")],
          [C("I ___ play the piano when I was a kid.", ["use to", "used to", "am used to"], 1),
           C("Did you ___ live here?", ["used to", "use to", "using to"], 1),
           C("I'm used to ___ up early.", ["wake", "waking", "woke"], 1),
           C("It's hard, but I'm ___ used to the new schedule.", ["getting", "using", "be"], 0),
           C("Antes había un parque aquí:", ["There used to be a park here.", "There was used a park here.", "There use to be a park here."], 0),
           F("She ___ (not) use to drink coffee.", "didn't|did not"),
           F("You'll get used ___ it.", "to"),
           F("I don't smoke ___. (ya no)", "anymore|any more"),
           T("Antes jugaba fútbol.", "I used to play soccer|I used to play football"),
           T("Estoy acostumbrado a trabajar tarde.", "I'm used to working late|I am used to working late")]),

        U("b1-10", "It must be…", "Modals of deduction",
          "Hacer deducciones sobre el presente con must, might, could y can't.",
          [("How sure are you?", """
            - **must** = estoy casi seguro de que SÍ → *She's not answering. She **must** be busy.*
            - **might / may / could** = es posible → *He **might** be at the gym.*
            - **can't** = estoy casi seguro de que NO → *That **can't** be true!*

            ⚠️ Para deducción negativa no se usa *mustn't*: se usa **can't**.
            """),
           ("With continuous", """
            Para algo que pasa ahora: **modal + be + -ing**

            > They **must be sleeping**; the lights are off.
            > She **might be working** late.
            """)],
          [("She must be tired after that trip.", "Debe estar cansada después de ese viaje."),
           ("He might be at the gym.", "Puede que esté en el gimnasio."),
           ("That can't be true!", "¡Eso no puede ser cierto!"),
           ("They must be sleeping.", "Deben estar durmiendo."),
           ("It could be a virus.", "Podría ser un virus."),
           ("You must be joking!", "¡Debes estar bromeando!"),
           ("She may not know the answer.", "Puede que ella no sepa la respuesta."),
           ("It can't be him. He's in Colombia.", "No puede ser él. Está en Colombia.")],
          [("must", "deber de (deducción segura)", "You must be hungry."),
           ("might", "podría / puede que", "It might rain."),
           ("may", "puede que", "She may be late."),
           ("could", "podría", "It could be anything."),
           ("can't", "no puede ser", "It can't be that expensive."),
           ("probably", "probablemente", "He's probably asleep."),
           ("certainly", "ciertamente", "It's certainly possible."),
           ("joke", "bromear / broma", "Are you joking?"),
           ("guess", "adivinar / suponer", "I guess you're right."),
           ("sure", "seguro", "Are you sure?"),
           ("clue", "pista", "I have no clue."),
           ("evidence", "evidencia", "There's no evidence.")],
          [C("She's not answering. She ___ be in a meeting.", ["must", "can't", "mustn't"], 0),
           C("That ___ be Juan. He's in Spain!", ["must", "can't", "might"], 1),
           C("I'm not sure where he is. He ___ be at home.", ["must", "might", "can't"], 1),
           C("Deducción negativa segura:", ["It mustn't be true.", "It can't be true.", "It doesn't must be true."], 1),
           C("The lights are off. They ___ sleeping.", ["must be", "must", "can be"], 0),
           F("You ___ be joking! (debes)", "must"),
           F("Take an umbrella; it ___ rain later. (posible)", "might|may|could"),
           T("¡Eso no puede ser cierto!", "That can't be true|That cannot be true|That can't be true!"),
           T("Debe estar cansada.", "She must be tired")]),
    ],
}
