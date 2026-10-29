from helpers import U, C, F, T, O

LEVEL = {
    "id": "A2",
    "title": "A2 · Básico",
    "description": "Cuentas lo que pasó, comparas cosas, haces planes y das consejos sencillos.",
    "units": [
        U("a2-01", "¿Dónde estabas?", "Pasado de to be: was / were",
          "Hablar de dónde estabas y cómo era algo en el pasado.",
          [("Was / were", """
            - I / he / she / it → **was**
            - you / we / they → **were**

            > I was at home yesterday. They were tired.
            > She **wasn't** at work. We **weren't** ready.
            > **Were** you at the party? — Yes, I was. / No, I wasn't.
            """),
           ("Expresiones de tiempo pasado", """
            - **yesterday** (ayer), **yesterday morning**
            - **last** night / week / month / year (la noche pasada, la semana pasada…)
            - two days **ago** (hace dos días) — *ago* va **después** del tiempo
            - **in** 2020 · **when** I was a child

            > Where were you last night?
            > I was born in 2004.  (nacer = **be born**)
            """)],
          [("I was at home yesterday.", "Estuve en casa ayer."),
           ("Where were you last night?", "¿Dónde estabas anoche?"),
           ("The movie was very good.", "La película estuvo muy buena."),
           ("We weren't at the party.", "No estuvimos en la fiesta."),
           ("Was it expensive?", "¿Estuvo caro?"),
           ("I was born in Antigua.", "Nací en Antigua."),
           ("They were in Guatemala two years ago.", "Estuvieron en Guatemala hace dos años."),
           ("It was cold this morning.", "Hizo frío esta mañana.")],
          [("yesterday", "ayer", "I was busy yesterday."),
           ("last night", "anoche", "Where were you last night?"),
           ("ago", "hace (tiempo)", "I was there two weeks ago."),
           ("be born", "nacer", "She was born in May."),
           ("party", "fiesta", "The party was fun."),
           ("weekend", "fin de semana", "How was your weekend?"),
           ("trip", "viaje", "The trip was amazing."),
           ("boring", "aburrido", "The class was boring."),
           ("fun", "divertido", "It was really fun."),
           ("cold", "frío", "It was very cold."),
           ("hot", "caliente / caluroso", "Yesterday was hot."),
           ("weather", "clima (del día)", "How was the weather?")],
          [C("I ___ at the gym yesterday.", ["were", "was", "am"], 1),
           C("___ they at home last night?", ["Was", "Were", "Did"], 1),
           C("Nací en 2004:", ["I born in 2004.", "I was born in 2004.", "I am born in 2004."], 1),
           C("Hace dos días:", ["ago two days", "two days ago", "before two days"], 1),
           C("The test ___ difficult. It was easy.", ["wasn't", "weren't", "didn't"], 0),
           C("How ___ your weekend? — It was great!", ["is", "were", "was"], 2),
           F("We ___ tired after the trip.", "were"),
           F("___ you at work yesterday? — No, I wasn't.", "Were"),
           T("¿Dónde estabas anoche?", "Where were you last night?"),
           T("La fiesta estuvo divertida.", "The party was fun")]),

        U("a2-02", "Lo que hice", "Pasado simple: verbos regulares",
          "Contar lo que hiciste ayer o el fin de semana.",
          [("Forma afirmativa", """
            Los verbos regulares agregan **-ed** (igual para todas las personas):

            - work → work**ed**, play → play**ed**
            - Terminados en e: + d → live → live**d**
            - Consonante + y → **-ied**: study → stud**ied**
            - Consonante-vocal-consonante corta: stop → sto**pped**, plan → pla**nned**

            Pronunciación de -ed: /t/ worked, /d/ played, /ɪd/ want**ed**, need**ed** (solo después de t o d).
            """),
           ("Negativo y preguntas con did", """
            - Negativo: **didn't** + verbo base → *I didn't watch TV.*
            - Pregunta: **Did** + sujeto + verbo base → *Did you call her?* — Yes, I did.
            - Con palabra interrogativa: *What did you do yesterday?*

            Después de did/didn't **nunca** va -ed: *Did you work?* (no *Did you worked?*)
            """)],
          [("I worked until eight yesterday.", "Trabajé hasta las ocho ayer."),
           ("We played soccer on Sunday.", "Jugamos fútbol el domingo."),
           ("She studied all night.", "Ella estudió toda la noche."),
           ("Did you call your mom?", "¿Llamaste a tu mamá?"),
           ("I didn't watch the game.", "No vi el partido."),
           ("They visited their grandparents.", "Visitaron a sus abuelos."),
           ("What did you do last weekend?", "¿Qué hiciste el fin de semana pasado?"),
           ("The bus stopped suddenly.", "El bus se detuvo de repente.")],
          [("visit", "visitar", "We visited the museum."),
           ("call", "llamar", "I called you last night."),
           ("play", "jugar / tocar", "He played the guitar."),
           ("watch", "ver", "We watched a movie."),
           ("travel", "viajar", "They traveled to Mexico."),
           ("arrive", "llegar", "The plane arrived late."),
           ("stay", "quedarse", "We stayed at a hotel."),
           ("decide", "decidir", "I decided to study more."),
           ("try", "intentar / probar", "I tried sushi for the first time."),
           ("stop", "detenerse / parar", "The rain stopped."),
           ("clean", "limpiar", "I cleaned my room."),
           ("until", "hasta", "I worked until midnight.")],
          [C("Pasado de 'study':", ["studyed", "studied", "studed"], 1),
           C("Pasado de 'stop':", ["stoped", "stopped", "stopt"], 1),
           C("___ you finish the project?", ["Do", "Did", "Were"], 1),
           C("She didn't ___ the email.", ["sent", "send", "sended"], 1, "Después de didn't, verbo base."),
           C("¿Qué -ed suena /ɪd/?", ["played", "wanted", "watched"], 1),
           C("What ___ you do on Saturday?", ["did", "do", "were"], 0),
           F("We ___ (travel) to Petén last year.", "traveled|travelled"),
           F("I ___ (not / like) the movie.", "didn't like|did not like"),
           F("___ she call you? — Yes, she did.", "Did"),
           T("¿Qué hiciste ayer?", "What did you do yesterday?"),
           T("No vi el partido.", "I didn't watch the game|I did not watch the game|I didn't see the game")]),

        U("a2-03", "Una historia", "Pasado simple: verbos irregulares",
          "Usar los verbos irregulares más comunes para contar historias.",
          [("Irregulares frecuentes", """
            No siguen la regla del -ed; hay que memorizarlos:

            - go → **went** · come → **came** · see → **saw**
            - have → **had** · do → **did** · make → **made**
            - get → **got** · take → **took** · give → **gave**
            - eat → **ate** · drink → **drank** · buy → **bought**
            - say → **said** · tell → **told** · think → **thought**
            - find → **found** · leave → **left** · meet → **met**
            - know → **knew** · write → **wrote** · read → **read** (se pronuncia "red")

            En negativo y preguntas se usa **did + verbo base**: *I didn't go. Did you see it?*
            """),
           ("Conectores para contar", """
            **First**, … **Then**, … **After that**, … **Finally**, …

            > First, we went to the market. Then we had lunch. After that, we took a tuk-tuk. Finally, we went home.
            """)],
          [("We went to Lake Atitlán last month.", "Fuimos al lago de Atitlán el mes pasado."),
           ("I saw a great movie.", "Vi una película muy buena."),
           ("She bought a new phone.", "Ella compró un teléfono nuevo."),
           ("They had a good time.", "La pasaron bien."),
           ("He told me the truth.", "Él me dijo la verdad."),
           ("I didn't go to class.", "No fui a clase."),
           ("Did you see the news?", "¿Viste las noticias?"),
           ("First we ate, then we went to the park.", "Primero comimos y luego fuimos al parque.")],
          [("went (go)", "fue / fui (ir)", "We went to the beach."),
           ("saw (see)", "vio / vi (ver)", "I saw her yesterday."),
           ("bought (buy)", "compró (comprar)", "He bought a car."),
           ("brought (bring)", "trajo (traer)", "She brought a cake."),
           ("thought (think)", "pensó (pensar)", "I thought it was easy."),
           ("found (find)", "encontró (encontrar)", "I found my keys."),
           ("left (leave)", "salió / dejó", "She left at six."),
           ("met (meet)", "conoció / se reunió", "I met my best friend at school."),
           ("took (take)", "tomó / llevó", "We took a lot of photos."),
           ("lost (lose)", "perdió (perder)", "He lost his wallet."),
           ("forgot (forget)", "olvidó (olvidar)", "I forgot my umbrella."),
           ("then", "luego / después", "Then we went home.")],
          [C("Pasado de 'buy':", ["buyed", "bought", "brought"], 1),
           C("Pasado de 'go':", ["goed", "gone", "went"], 2),
           C("I ___ my keys yesterday.", ["lose", "lost", "losed"], 1),
           C("Did you ___ the email?", ["wrote", "write", "written"], 1),
           C("She ___ me a gift.", ["gave", "gived", "give"], 0),
           C("Pasado de 'think':", ["thinked", "thought", "taught"], 1),
           F("We ___ (eat) pupusas last night.", "ate"),
           F("I ___ (meet) her in 2022.", "met"),
           F("He didn't ___ (come) to the party.", "come"),
           T("Fuimos al mercado.", "We went to the market"),
           T("Compré un teléfono nuevo.", "I bought a new phone")]),

        U("a2-04", "Más grande, el mejor", "Comparativos y superlativos",
          "Comparar personas, lugares y cosas.",
          [("Comparativos", """
            - Adjetivos cortos: + **-er than** → tall → tall**er than**, cheap → cheap**er than**
            - Terminados en y: **-ier** → easy → eas**ier**, happy → happ**ier**
            - Doble consonante: big → bi**gger**, hot → ho**tter**
            - Adjetivos largos: **more … than** → **more expensive than**, **more interesting than**
            - Irregulares: good → **better**, bad → **worse**, far → **farther/further**

            Igualdad: **as … as** → *She's as tall as her brother.*
            """),
           ("Superlativos", """
            - Cortos: **the -est** → the tall**est**, the bigg**est**, the eas**iest**
            - Largos: **the most** → the most beautiful
            - Irregulares: **the best**, **the worst**

            > It's the best restaurant **in** the city. (con lugares se usa **in**, no *of*)
            """)],
          [("My brother is taller than me.", "Mi hermano es más alto que yo."),
           ("This phone is cheaper than that one.", "Este teléfono es más barato que ese."),
           ("English is easier than Chinese.", "El inglés es más fácil que el chino."),
           ("Guatemala City is bigger than Antigua.", "La Ciudad de Guatemala es más grande que Antigua."),
           ("It's the best pizza in town.", "Es la mejor pizza del pueblo."),
           ("Today is worse than yesterday.", "Hoy está peor que ayer."),
           ("She's as smart as her sister.", "Ella es tan lista como su hermana."),
           ("This is the most expensive hotel in the city.", "Este es el hotel más caro de la ciudad.")],
          [("tall", "alto (persona)", "He's very tall."),
           ("short", "bajo / corto", "My hair is short."),
           ("big", "grande", "It's a big city."),
           ("fast", "rápido", "My car is fast."),
           ("slow", "lento", "The internet is slow today."),
           ("easy", "fácil", "This exercise is easy."),
           ("difficult", "difícil", "C1 is more difficult than B1."),
           ("better", "mejor", "This version is better."),
           ("worse", "peor", "The traffic is worse today."),
           ("the best", "el/la mejor", "You're the best!"),
           ("the worst", "el/la peor", "It was the worst day."),
           ("dangerous", "peligroso", "That road is dangerous.")],
          [C("My car is ___ than yours.", ["fast", "faster", "more fast"], 1),
           C("This book is ___ than the movie.", ["interestinger", "more interesting", "most interesting"], 1),
           C("Comparativo de 'good':", ["gooder", "better", "best"], 1),
           C("It's ___ day of my life!", ["the happiest", "the happyest", "happiest"], 0),
           C("Mount Everest is the highest mountain ___ the world.", ["of", "in", "than"], 1),
           C("She's as tall ___ her mother.", ["than", "as", "like"], 1),
           F("Today is ___ (hot) than yesterday.", "hotter"),
           F("This is the ___ (bad) movie I've ever seen.", "worst"),
           F("Chinese is more difficult ___ English.", "than"),
           T("Mi hermana es más alta que yo.", "My sister is taller than me|My sister is taller than I am"),
           T("Es la mejor pizza de la ciudad.", "It's the best pizza in the city|It is the best pizza in the city")]),

        U("a2-05", "Planes y predicciones", "Futuro: going to y will",
          "Hablar de planes, decisiones del momento y predicciones.",
          [("Be going to", """
            **am/is/are + going to + verbo**

            - **Planes ya decididos**: *I'm going to visit my grandma on Sunday.*
            - **Predicciones con evidencia**: *Look at those clouds. It's going to rain.*

            > Are you going to study tonight? — Yes, I am.
            """),
           ("Will", """
            **will ('ll) + verbo** (igual para todas las personas). Negativo: **won't**.

            - **Decisiones en el momento**: *The phone is ringing. I'll answer it.*
            - **Promesas y ofrecimientos**: *I'll help you. I won't tell anyone.*
            - **Predicciones / opiniones**: *I think it will be a great year.*
            """),
           ("¿Cuál uso?", """
            Si ya lo **planeaste** antes → **going to**.
            Si lo **decides ahora** u ofreces algo → **will**.

            > A: We don't have milk. B: Really? **I'll** buy some. (decisión ahora)
            > I'm **going to** buy milk after work. (plan)
            """)],
          [("I'm going to study tonight.", "Voy a estudiar esta noche."),
           ("It's going to rain.", "Va a llover."),
           ("I'll help you with that.", "Te ayudo con eso."),
           ("We're going to travel in December.", "Vamos a viajar en diciembre."),
           ("I won't tell anyone.", "No le diré a nadie."),
           ("I think you'll like it.", "Creo que te va a gustar."),
           ("Are you going to buy a car?", "¿Vas a comprar un carro?"),
           ("Don't worry, I'll call you later.", "No te preocupes, te llamo más tarde.")],
          [("plan", "plan / planear", "What are your plans for the weekend?"),
           ("tomorrow", "mañana (día)", "I'll see you tomorrow."),
           ("next week", "la próxima semana", "We're going to travel next week."),
           ("soon", "pronto", "See you soon!"),
           ("later", "más tarde", "I'll call you later."),
           ("promise", "prometer / promesa", "I promise I'll help."),
           ("probably", "probablemente", "It will probably rain."),
           ("maybe", "quizás", "Maybe I'll go."),
           ("future", "futuro", "What will you do in the future?"),
           ("win", "ganar", "I think they'll win."),
           ("forget", "olvidar", "I won't forget."),
           ("worry", "preocuparse", "Don't worry!")],
          [C("Look at the sky! It ___ rain.", ["will", "is going to", "going to"], 1, "Hay evidencia → **going to**."),
           C("A: I'm thirsty. B: ___ get you some water.", ["I'm going to", "I'll", "I go to"], 1, "Ofrecimiento en el momento → **will**."),
           C("We ___ visit Tikal next month. We have the tickets.", ["will", "are going to", "go"], 1, "Plan decidido → **going to**."),
           C("Negativo de 'will':", ["willn't", "won't", "don't will"], 1),
           C("I think she ___ the exam.", ["will pass", "passes will", "is pass"], 0),
           F("I ___ (not) tell anyone. I promise.", "won't|will not"),
           F("Are you going ___ study tonight?", "to"),
           F("She's ___ to buy a new laptop.", "going"),
           T("Voy a estudiar esta noche.", "I'm going to study tonight|I am going to study tonight"),
           T("Te ayudo.", "I'll help you|I will help you")]),

        U("a2-06", "Mi agenda", "Presente continuo para futuro, fechas y preposiciones de tiempo",
          "Hablar de citas y planes con fecha y hora.",
          [("Presente continuo con sentido futuro", """
            Para **citas y planes ya organizados** (con hora, lugar o personas) se usa mucho el presente continuo:

            > I'm meeting Ana at five.
            > We're flying to Cancún on Friday.
            > What are you doing this weekend?
            """),
           ("In / on / at con tiempo", """
            - **at** + hora exacta y momentos: at 7:00, at noon, at night, at the weekend (británico)
            - **on** + días y fechas: on Monday, on May 5th, on my birthday, on the weekend (americano)
            - **in** + meses, años, estaciones y partes del día: in March, in 2027, in the morning

            Sin preposición con **this / next / last / every**: *next Monday*, *this week*.
            """),
           ("Fechas", """
            En inglés se usan **números ordinales**: first (1st), second (2nd), third (3rd), fourth (4th), fifth (5th)… twentieth (20th), twenty-first (21st).

            > My birthday is on **May 5th** (se lee "May fifth").
            > What's the date today? — It's **October 29th**.
            """)],
          [("I'm meeting my friends at six.", "Voy a ver a mis amigos a las seis."),
           ("What are you doing this weekend?", "¿Qué vas a hacer este fin de semana?"),
           ("We're leaving on Friday.", "Nos vamos el viernes."),
           ("My birthday is on July 3rd.", "Mi cumpleaños es el 3 de julio."),
           ("The meeting is at ten in the morning.", "La reunión es a las diez de la mañana."),
           ("I was born in 2004.", "Nací en 2004."),
           ("See you next Tuesday.", "Nos vemos el próximo martes."),
           ("I have an appointment on Monday.", "Tengo una cita el lunes.")],
          [("appointment", "cita (médico, trámite)", "I have a doctor's appointment."),
           ("meeting", "reunión", "The meeting starts at nine."),
           ("schedule", "horario / agenda", "My schedule is full."),
           ("birthday", "cumpleaños", "Happy birthday!"),
           ("Monday", "lunes", "See you on Monday."),
           ("weekend", "fin de semana", "I'm free this weekend."),
           ("free", "libre / gratis", "Are you free tomorrow?"),
           ("noon", "mediodía", "Let's meet at noon."),
           ("first", "primero", "It's the first day."),
           ("third", "tercero", "My office is on the third floor."),
           ("leave", "irse / salir", "We're leaving at five."),
           ("date", "fecha / cita romántica", "What's the date today?")],
          [C("I have a meeting ___ Monday.", ["in", "on", "at"], 1),
           C("The class is ___ 9:30.", ["at", "on", "in"], 0),
           C("I was born ___ August.", ["on", "at", "in"], 2),
           C("Elige la correcta:", ["See you on next Friday.", "See you next Friday.", "See you in next Friday."], 1,
             "Con **next** no se usa preposición."),
           C("Plan ya organizado:", ["I meet Carlos tomorrow at 5.", "I'm meeting Carlos tomorrow at 5.", "I meeting Carlos tomorrow."], 1),
           C("¿Cómo se escribe 22º?", ["22th", "22nd", "22rd"], 1),
           F("What ___ you doing tonight?", "are"),
           F("My birthday is ___ June 12th.", "on"),
           T("Voy a ver a Ana a las cinco.", "I'm meeting Ana at five|I am meeting Ana at five|I'm seeing Ana at five|I'm meeting Ana at 5"),
           T("Tengo una cita el lunes.", "I have an appointment on Monday")]),

        U("a2-07", "¿Alguna vez…?", "Introducción al present perfect",
          "Hablar de experiencias y de cosas recién hechas.",
          [("Forma", """
            **have / has + participio pasado**

            - Regulares: participio = -ed → work**ed**, visit**ed**
            - Irregulares frecuentes: be → **been**, go → **gone/been**, see → **seen**, eat → **eaten**, do → **done**, make → **made**, have → **had**, write → **written**, take → **taken**

            > I've (I have) visited Mexico. She's (she has) finished.
            > I **haven't** seen it. **Have** you ever tried sushi?
            """),
           ("Experiencias: ever / never", """
            Para hablar de experiencias de la vida **sin decir cuándo**:

            > **Have you ever** been to the US? — Yes, I have. / No, I haven't.
            > I've **never** eaten sushi.

            Si dices **cuándo** pasó, cambia a pasado simple: *I went there in 2023.*
            """),
           ("Just / already / yet", """
            - **just** = acabar de → *I've just arrived.* (acabo de llegar)
            - **already** = ya (afirmativo) → *I've already eaten.*
            - **yet** = ya (pregunta) / todavía (negativo), va al final → *Have you finished yet? I haven't finished yet.*
            """)],
          [("Have you ever been to the beach?", "¿Alguna vez has ido a la playa?"),
           ("I've never seen snow.", "Nunca he visto nieve."),
           ("She has visited Mexico three times.", "Ella ha visitado México tres veces."),
           ("I've just finished my homework.", "Acabo de terminar mi tarea."),
           ("Have you eaten yet?", "¿Ya comiste?"),
           ("We haven't decided yet.", "Todavía no hemos decidido."),
           ("He's already left.", "Él ya se fue."),
           ("I went there last year.", "Fui allí el año pasado.")],
          [("ever", "alguna vez", "Have you ever flown?"),
           ("never", "nunca", "I've never been to Europe."),
           ("just", "acabar de", "She's just called."),
           ("already", "ya", "I've already done it."),
           ("yet", "ya / todavía (final)", "Not yet."),
           ("been (be)", "estado / ido", "I've been to Belize."),
           ("seen (see)", "visto", "Have you seen my phone?"),
           ("eaten (eat)", "comido", "I've never eaten insects."),
           ("done (do)", "hecho", "Have you done your homework?"),
           ("written (write)", "escrito", "I've written three emails."),
           ("snow", "nieve", "I want to see snow."),
           ("experience", "experiencia", "It was a great experience.")],
          [C("___ you ever been to Mexico?", ["Did", "Have", "Has"], 1),
           C("She ___ never tried tacos al pastor.", ["have", "has", "is"], 1),
           C("I've ___ seen that movie. (nunca)", ["ever", "never", "yet"], 1),
           C("Have you finished ___?", ["already", "yet", "just"], 1),
           C("I ___ to Tikal in 2023.", ["have gone", "went", "have been"], 1, "Con fecha (in 2023) → pasado simple."),
           C("Participio de 'write':", ["wrote", "written", "writed"], 1),
           F("I've ___ arrived. (acabo de)", "just"),
           F("We haven't decided ___. (todavía)", "yet"),
           F("Have you ___ (see) my keys?", "seen"),
           T("Nunca he visto nieve.", "I've never seen snow|I have never seen snow"),
           T("¿Ya comiste?", "Have you eaten yet?|Did you eat yet?|Did you eat already?|Have you already eaten?")]),

        U("a2-08", "Deberías…", "Must, have to y should",
          "Hablar de obligaciones, reglas y dar consejos.",
          [("Have to / must", """
            - **have to**: obligación (muy común en conversación). *I have to work tomorrow. She has to study.*
            - **must**: obligación fuerte o regla, más formal o personal. *You must wear a seatbelt.*
            - Pregunta: **Do you have to** work on Saturdays?
            """),
           ("¡Cuidado con el negativo!", """
            - **don't have to** = no es necesario → *You don't have to come.* (puedes venir si quieres)
            - **mustn't** = está prohibido → *You mustn't smoke here.*

            Significan cosas muy distintas.
            """),
           ("Should para consejos", """
            **should / shouldn't + verbo** = deberías / no deberías:

            > You should sleep more. You shouldn't drink so much coffee.
            > What **should** I do?

            Para opinar suave: *I think you should talk to her.*
            """)],
          [("I have to work tomorrow.", "Tengo que trabajar mañana."),
           ("You must wear a helmet.", "Debes usar casco."),
           ("You don't have to pay. It's free.", "No tienes que pagar. Es gratis."),
           ("You mustn't use your phone in the exam.", "No debes usar el celular en el examen."),
           ("You should see a doctor.", "Deberías ir al médico."),
           ("She shouldn't stay up so late.", "Ella no debería desvelarse tanto."),
           ("Do we have to bring anything?", "¿Tenemos que llevar algo?"),
           ("What should I wear?", "¿Qué debería ponerme?")],
          [("have to", "tener que", "I have to go."),
           ("must", "deber (obligación)", "You must be on time."),
           ("should", "deberías (consejo)", "You should rest."),
           ("rule", "regla", "Those are the rules."),
           ("allowed", "permitido", "Phones aren't allowed here."),
           ("wear", "usar (ropa)", "You have to wear a uniform."),
           ("pay", "pagar", "Do I have to pay now?"),
           ("advice", "consejo (incontable)", "Can you give me some advice?"),
           ("rest", "descansar", "You should rest."),
           ("doctor", "médico", "Go to the doctor."),
           ("on time", "a tiempo", "Please be on time."),
           ("stay up late", "desvelarse", "I stayed up late last night.")],
          [C("She ___ work on Saturdays.", ["have to", "has to", "must to"], 1),
           C("No es necesario que vengas:", ["You mustn't come.", "You don't have to come.", "You shouldn't come."], 1),
           C("Está prohibido fumar:", ["You don't have to smoke here.", "You mustn't smoke here.", "You have to smoke here."], 1),
           C("Consejo: you look tired. You ___ go to bed early.", ["should", "must", "have"], 0),
           C("___ I bring my ID?", ["Do I have to", "Have I to", "Must I to"], 0),
           C("Elige la correcta:", ["You should to rest.", "You should rest.", "You should resting."], 1),
           F("We ___ to finish this today. (tenemos que)", "have"),
           F("You ___ eat so much sugar. (no deberías)", "shouldn't|should not"),
           T("Tengo que trabajar mañana.", "I have to work tomorrow"),
           T("Deberías ir al médico.", "You should see a doctor|You should go to the doctor")]),

        U("a2-09", "Muy rápido, demasiado tarde", "Adverbios, too/enough y pronombres de objeto",
          "Describir cómo se hacen las cosas y decir si algo es suficiente.",
          [("Adverbios de modo", """
            Dicen **cómo** se hace algo. Se forman con **-ly** (como el *-mente* del español):

            - slow → slow**ly**, careful → careful**ly**, easy → eas**ily**
            - Irregulares: good → **well**, fast → **fast**, hard → **hard**, late → **late**

            > She speaks English **well**. (no *good*)
            > He drives **carefully**.
            """),
           ("Too y enough", """
            - **too + adjetivo** = demasiado (problema) → *It's too expensive.*
            - **adjetivo + enough** = suficientemente → *He isn't old enough to drive.*
            - **enough + sustantivo** = suficiente → *We don't have enough time.*
            """),
           ("Pronombres de objeto", """
            Van **después** del verbo o preposición:

            - I → **me** · you → **you** · he → **him** · she → **her**
            - it → **it** · we → **us** · they → **them**

            > Call **me** later. I love **them**. This is for **her**.
            """)],
          [("She speaks English very well.", "Ella habla inglés muy bien."),
           ("Please drive carefully.", "Maneja con cuidado, por favor."),
           ("This coffee is too hot.", "Este café está demasiado caliente."),
           ("I'm not tall enough.", "No soy lo suficientemente alto."),
           ("We don't have enough money.", "No tenemos suficiente dinero."),
           ("Can you help me?", "¿Me puedes ayudar?"),
           ("I called them yesterday.", "Los llamé ayer."),
           ("He works hard every day.", "Él trabaja duro todos los días.")],
          [("slowly", "despacio", "Speak slowly, please."),
           ("quickly", "rápidamente", "She finished quickly."),
           ("carefully", "con cuidado", "Read the instructions carefully."),
           ("well", "bien (adverbio)", "You did it well."),
           ("hard", "duro / difícil", "He works hard."),
           ("loudly", "en voz alta / fuerte", "Don't talk so loudly."),
           ("quietly", "en silencio / bajito", "She speaks quietly."),
           ("too", "demasiado", "It's too late."),
           ("enough", "suficiente", "Is that enough?"),
           ("almost", "casi", "I'm almost ready."),
           ("together", "juntos", "Let's study together."),
           ("again", "otra vez", "Say it again, please.")],
          [C("She sings very ___.", ["good", "well", "goodly"], 1),
           C("Drive ___! The road is wet.", ["careful", "carefully", "carefuly"], 1),
           C("I can't buy it. It's ___ expensive.", ["too", "enough", "very enough"], 0),
           C("He's 15. He isn't old ___ to drive.", ["too", "enough", "so"], 1),
           C("Call ___ when you arrive. (a mí)", ["I", "my", "me"], 2),
           C("I love my parents. I visit ___ every Sunday.", ["they", "them", "their"], 1),
           F("We don't have ___ time.", "enough"),
           F("Speak ___ (slow), please.", "slowly"),
           T("Este café está demasiado caliente.", "This coffee is too hot"),
           T("Habla despacio, por favor.", "Speak slowly, please|Please speak slowly")]),

        U("a2-10", "Mientras tanto…", "Pasado continuo y when / while",
          "Describir lo que estaba pasando cuando ocurrió algo.",
          [("Forma", """
            **was / were + verbo -ing**

            > I was sleeping at 11 p.m.
            > They were playing when it started to rain.
            > What **were** you doing? — I **was** working.
            """),
           ("Pasado continuo + pasado simple", """
            La acción **larga** (de fondo) va en continuo; la acción **corta** que la interrumpe, en simple:

            > I **was cooking** when the phone **rang**.
            > **While** we **were walking**, we **saw** an accident.

            - **when** + acción corta (simple)
            - **while** + acción larga (continuo)
            """)],
          [("I was sleeping when you called.", "Estaba durmiendo cuando llamaste."),
           ("What were you doing at eight?", "¿Qué estabas haciendo a las ocho?"),
           ("It was raining all day.", "Estuvo lloviendo todo el día."),
           ("While I was driving, I saw an accident.", "Mientras manejaba, vi un accidente."),
           ("They were watching TV when the lights went out.", "Estaban viendo tele cuando se fue la luz."),
           ("She wasn't listening.", "Ella no estaba escuchando."),
           ("We were having dinner at nine.", "Estábamos cenando a las nueve."),
           ("He fell while he was running.", "Se cayó mientras corría.")],
          [("while", "mientras", "While I was working, he called."),
           ("when", "cuando", "When I arrived, she was leaving."),
           ("suddenly", "de repente", "Suddenly, the lights went out."),
           ("accident", "accidente", "I saw an accident."),
           ("ring (rang)", "sonar (teléfono)", "The phone rang."),
           ("fall (fell)", "caer(se)", "He fell off his bike."),
           ("happen", "pasar / suceder", "What happened?"),
           ("break (broke)", "romper", "I broke my phone."),
           ("hear (heard)", "oír", "I heard a noise."),
           ("noise", "ruido", "What's that noise?"),
           ("drive (drove)", "manejar", "We drove to Xela."),
           ("lights", "luces", "The lights went out.")],
          [C("I ___ TV when you called.", ["watched", "was watching", "were watching"], 1),
           C("They ___ dinner at 8 p.m.", ["was having", "were having", "had having"], 1),
           C("While I was walking, I ___ my teacher.", ["was seeing", "saw", "see"], 1),
           C("___ I was cooking, the lights went out.", ["When", "While", "During"], 1, "Acción larga → **while**."),
           C("What ___ you doing at midnight?", ["was", "were", "did"], 1),
           F("She ___ (not / listen) to the teacher.", "wasn't listening|was not listening"),
           F("It ___ (rain) when we left.", "was raining"),
           F("He ___ (fall) while he was running.", "fell"),
           T("Estaba durmiendo cuando llamaste.", "I was sleeping when you called"),
           T("¿Qué estabas haciendo?", "What were you doing?")]),
    ],
}
