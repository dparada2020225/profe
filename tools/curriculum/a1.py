from helpers import U, C, F, T, O

LEVEL = {
    "id": "A1",
    "title": "A1 · Principiante",
    "description": "Te presentas, hablas de ti, tu familia y tu rutina con frases sencillas.",
    "units": [
        U("a1-01", "Hola, soy…", "Saludos y el verbo to be (I am / you are)",
          "Saludar, presentarte y decir de dónde eres.",
          [("El verbo to be", """
            **To be** significa *ser* o *estar*. En presente tiene tres formas:

            - I **am** (yo soy/estoy)
            - you / we / they **are**
            - he / she / it **is**

            En conversación casi siempre se usan contracciones: **I'm**, **you're**, **he's**, **she's**, **it's**, **we're**, **they're**.

            > I'm Denil. I'm from Guatemala.
            > She's a teacher.
            > We're students.
            """),
           ("Saludos y despedidas", """
            - **Hello / Hi** → hola (Hi es más informal)
            - **Good morning / Good afternoon / Good evening** → buenos días / buenas tardes / buenas noches (al llegar)
            - **Good night** → buenas noches (solo al despedirte o irte a dormir)
            - **Nice to meet you** → mucho gusto
            - **See you later / Bye** → nos vemos / adiós

            Para preguntar el nombre: **What's your name?** → **My name is…** o **I'm…**
            Para preguntar el origen: **Where are you from?** → **I'm from…**
            """),
           ("Ojo: el sujeto siempre va", """
            En español decimos *soy estudiante* sin "yo". En inglés **el sujeto es obligatorio**: no se dice *Am a student*, sino **I'm a student**.

            Con profesiones se usa **a / an**: **I'm a developer**, **She's an engineer**.
            """)],
          [("Hi! I'm Denil.", "¡Hola! Soy Denil."),
           ("Nice to meet you.", "Mucho gusto."),
           ("Where are you from?", "¿De dónde eres?"),
           ("I'm from Guatemala.", "Soy de Guatemala."),
           ("She's a teacher.", "Ella es maestra."),
           ("We're friends.", "Somos amigos."),
           ("They're in the classroom.", "Ellos están en el salón."),
           ("It's a beautiful city.", "Es una ciudad hermosa.")],
          [("hello", "hola", "Hello, how are you?"),
           ("name", "nombre", "My name is Ana."),
           ("friend", "amigo/a", "He's my best friend."),
           ("student", "estudiante", "I'm a university student."),
           ("teacher", "maestro/a", "Our teacher is very nice."),
           ("city", "ciudad", "Antigua is a beautiful city."),
           ("country", "país", "Guatemala is my country."),
           ("from", "de (origen)", "Where are you from?"),
           ("morning", "mañana (parte del día)", "Good morning, everyone!"),
           ("night", "noche", "Good night, see you tomorrow."),
           ("meet", "conocer (a alguien)", "Nice to meet you."),
           ("job", "trabajo", "I love my job.")],
          [C("I ___ a student.", ["is", "am", "are"], 1, "Con **I** siempre se usa **am**."),
           C("She ___ from Mexico.", ["am", "are", "is"], 2, "He / she / it → **is**."),
           C("They ___ my friends.", ["is", "are", "am"], 1, "We / you / they → **are**."),
           C("¿Cómo dices 'mucho gusto'?", ["Nice to meet you", "Good night", "See you"], 0),
           C("Son las 9 p. m. y llegas a una reunión. Dices:", ["Good night", "Good evening", "Good morning"], 1,
             "**Good night** es solo para despedirse; al llegar se dice **Good evening**."),
           F("___ name is Carlos.", "My", "Mi nombre = **My** name."),
           F("We ___ from Guatemala.", "are|'re"),
           F("He ___ an engineer.", "is|'s"),
           T("Soy de Antigua.", "I'm from Antigua|I am from Antigua"),
           T("Ella es mi amiga.", "She's my friend|She is my friend")]),

        U("a1-02", "¿Eres…? No, no soy…", "To be: negativo, preguntas y números",
          "Hacer preguntas de sí/no, negar y usar números del 0 al 100.",
          [("Negativo", """
            Se agrega **not** después de to be:

            - I'm **not** tired.
            - He **isn't** (is not) here.
            - We **aren't** (are not) ready.

            Ojo: no existe *amn't*. Con I se dice **I'm not**.
            """),
           ("Preguntas", """
            Se invierte el orden: el verbo va **antes** del sujeto.

            > Are you a student? — Yes, I am. / No, I'm not.
            > Is she your sister? — Yes, she is. / No, she isn't.

            En respuestas cortas afirmativas **no** se usa contracción: *Yes, I am* (no *Yes, I'm*).

            Preguntas con palabra interrogativa: **How old are you?** (¿cuántos años tienes?) → **I'm 20.** En inglés la edad se dice con *be*, no con *have*.
            """),
           ("Números", """
            - 1-12: one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve
            - 13-19 terminan en **-teen**: thirteen, fourteen, fifteen…
            - Decenas terminan en **-ty**: twenty, thirty, forty (¡sin u!), fifty…
            - 21 = twenty-one, 99 = ninety-nine, 100 = one hundred

            Cuidado al escuchar: **thirteen** (13) tiene el acento al final; **thirty** (30) al inicio.
            """)],
          [("I'm not tired.", "No estoy cansado."),
           ("Are you ready?", "¿Estás listo?"),
           ("Yes, I am.", "Sí, lo estoy."),
           ("Is he your brother?", "¿Él es tu hermano?"),
           ("No, he isn't. He's my cousin.", "No. Es mi primo."),
           ("How old are you?", "¿Cuántos años tienes?"),
           ("I'm twenty years old.", "Tengo veinte años."),
           ("It isn't expensive.", "No es caro.")],
          [("tired", "cansado/a", "I'm tired after work."),
           ("ready", "listo/a", "Are you ready to go?"),
           ("happy", "feliz", "She's very happy today."),
           ("busy", "ocupado/a", "Sorry, I'm busy now."),
           ("hungry", "con hambre", "Are you hungry?"),
           ("old", "viejo / de edad", "How old is your dog?"),
           ("young", "joven", "My sister is very young."),
           ("expensive", "caro/a", "This phone is expensive."),
           ("cheap", "barato/a", "The food here is cheap."),
           ("brother", "hermano", "My brother is in Colombia."),
           ("sister", "hermana", "My sister is an architect."),
           ("late", "tarde / retrasado", "Sorry, I'm late!")],
          [C("___ you from Guatemala?", ["Is", "Are", "Am"], 1),
           C("She ___ at home. She's at work.", ["isn't", "aren't", "not"], 0),
           C("Respuesta correcta a 'Are you hungry?'", ["Yes, I'm.", "Yes, I am.", "Yes, I hungry."], 1,
             "En respuestas cortas afirmativas no se contrae: **Yes, I am.**"),
           C("¿Cómo se escribe 40?", ["fourty", "forty", "fourteen"], 1, "**forty** no lleva u."),
           C("Tengo 25 años.", ["I have 25 years.", "I'm 25 years old.", "I am 25 years."], 1,
             "La edad se dice con **be**: I'm 25 (years old)."),
           F("We ___ ready. Wait a minute!", "aren't|are not|'re not"),
           F("___ he your teacher? — Yes, he is.", "Is"),
           F("I ___ not late.", "am|'m"),
           T("¿Estás ocupado?", "Are you busy?"),
           T("No es caro.", "It isn't expensive|It's not expensive|It is not expensive")]),

        U("a1-03", "Cosas a mi alrededor", "Artículos, plurales y this/that/these/those",
          "Nombrar objetos, usar a/an/the y señalar cosas cerca o lejos.",
          [("A / an / the", """
            - **a** antes de sonido de consonante: a book, a car, **a university** (suena "iu")
            - **an** antes de sonido de vocal: an apple, an idea, **an hour** (la h no suena)
            - **the** = el/la/los/las, cuando ya sabemos de cuál hablamos.

            > I have a dog. The dog is black.
            """),
           ("Plurales", """
            - La mayoría: + **s** → book**s**, car**s**
            - Terminados en s, sh, ch, x: + **es** → bus**es**, watch**es**, box**es**
            - Consonante + y → **ies**: city → cit**ies** (pero day → day**s**)
            - Irregulares: man → **men**, woman → **women**, child → **children**, person → **people**, foot → **feet**, tooth → **teeth**

            Los adjetivos **no** cambian en plural: *two big houses* (no *bigs*).
            """),
           ("This, that, these, those", """
            - Cerca, singular: **this** (este/esta)
            - Cerca, plural: **these** (estos/estas)
            - Lejos, singular: **that** (ese/aquel)
            - Lejos, plural: **those** (esos/aquellos)

            > This is my phone. Those are my keys.
            """)],
          [("This is my laptop.", "Esta es mi laptop."),
           ("Those cars are expensive.", "Esos carros son caros."),
           ("I need an umbrella.", "Necesito un paraguas."),
           ("The children are in the park.", "Los niños están en el parque."),
           ("These shoes are new.", "Estos zapatos son nuevos."),
           ("That is a good idea.", "Esa es una buena idea."),
           ("There are three people here.", "Hay tres personas aquí."),
           ("It's an old house.", "Es una casa vieja.")],
          [("book", "libro", "This book is interesting."),
           ("phone", "teléfono", "My phone is on the table."),
           ("key", "llave", "Where are my keys?"),
           ("table", "mesa", "The keys are on the table."),
           ("chair", "silla", "This chair is comfortable."),
           ("bag", "bolsa / mochila", "My bag is heavy."),
           ("umbrella", "paraguas", "Take an umbrella, it's raining."),
           ("child / children", "niño / niños", "The children are at school."),
           ("person / people", "persona / personas", "Many people live here."),
           ("idea", "idea", "That's a great idea!"),
           ("new", "nuevo/a", "I have a new job."),
           ("small", "pequeño/a", "It's a small apartment.")],
          [C("I have ___ apple.", ["a", "an", "the"], 1, "apple empieza con sonido de vocal → **an**."),
           C("She studies at ___ university.", ["an", "a", "—"], 1, "university suena 'iu' (consonante) → **a**."),
           C("Plural de 'city':", ["citys", "cities", "cityes"], 1),
           C("Plural de 'child':", ["childs", "children", "childrens"], 1),
           C("(Lejos, plural) ___ are my friends over there.", ["These", "This", "Those"], 2),
           C("Elige la correcta:", ["two bigs houses", "two big houses", "two big house"], 1,
             "Los adjetivos no tienen plural."),
           F("___ is my brother. (cerca, singular)", "This"),
           F("Wait one ___. (hora; usa artículo + hour)", "an hour", "**an hour**: la h no suena."),
           T("Estos libros son nuevos.", "These books are new"),
           T("Necesito una idea.", "I need an idea")]),

        U("a1-04", "Mi familia", "Posesivos, 's y pronombres",
          "Hablar de tu familia y de quién es cada cosa.",
          [("Adjetivos posesivos", """
            Van antes del sustantivo y **no** cambian en plural:

            - I → **my** · you → **your** · he → **his** · she → **her**
            - it → **its** · we → **our** · they → **their**

            > My parents live in Ciudad Vieja. Their house is big.

            Cuidado: **its** (de él/ella, cosa) ≠ **it's** (it is).
            """),
           ("El 's para decir 'de'", """
            En español: *el carro de mi papá*. En inglés: **my dad's car**.

            - Singular: my sister**'s** room
            - Plural con s: my parents**'** house (solo apóstrofo)
            - Plural irregular: the children**'s** toys
            """),
           ("La familia", """
            - mother / father → **parents**
            - son / daughter → **children**
            - brother / sister → **siblings**
            - grandmother / grandfather → **grandparents**
            - uncle / aunt / cousin / nephew / niece
            - husband / wife

            Pregunta útil: **Do you have any brothers or sisters?**
            """)],
          [("This is my family.", "Esta es mi familia."),
           ("My mother's name is Rosa.", "El nombre de mi mamá es Rosa."),
           ("Their house is in Antigua.", "Su casa (de ellos) está en Antigua."),
           ("Her brother is a doctor.", "El hermano de ella es doctor."),
           ("Our dog is very friendly.", "Nuestro perro es muy amigable."),
           ("My parents' car is red.", "El carro de mis papás es rojo."),
           ("I have two cousins.", "Tengo dos primos."),
           ("What's your sister's name?", "¿Cómo se llama tu hermana?")],
          [("mother", "madre", "My mother is a nurse."),
           ("father", "padre", "My father works a lot."),
           ("parents", "padres (papá y mamá)", "My parents are from Antigua."),
           ("son", "hijo", "Their son is ten years old."),
           ("daughter", "hija", "She has a daughter."),
           ("siblings", "hermanos (en general)", "I have three siblings."),
           ("grandmother", "abuela", "My grandmother cooks very well."),
           ("uncle", "tío", "My uncle lives in the US."),
           ("aunt", "tía", "My aunt is a teacher."),
           ("cousin", "primo/a", "My cousin is my best friend."),
           ("husband", "esposo", "Her husband is Canadian."),
           ("wife", "esposa", "His wife is a doctor.")],
          [C("Carlos and ___ wife live in Mixco.", ["her", "his", "their"], 1, "Esposa de él → **his** wife."),
           C("Ana loves ___ job.", ["his", "her", "its"], 1),
           C("The dog is eating ___ food.", ["it's", "its", "his"], 1, "**its** = posesivo; **it's** = it is."),
           C("El carro de mi hermano:", ["my brother car", "my brother's car", "the car my brother"], 1),
           C("La casa de mis papás:", ["my parents's house", "my parents' house", "my parent house"], 1),
           C("Los hijos de tus tíos son tus…", ["nephews", "cousins", "siblings"], 1),
           F("We love ___ city. (nuestra)", "our"),
           F("They are students. ___ teacher is Mr. Lopez.", "Their"),
           T("Mi papá se llama José.", "My father's name is José|My dad's name is José|My father's name is Jose|My dad's name is Jose"),
           T("Tengo dos hermanos.", "I have two brothers|I have two siblings")]),

        U("a1-05", "Mi día", "Presente simple: afirmativo y la hora",
          "Describir tu rutina diaria y decir la hora.",
          [("Presente simple", """
            Se usa para **rutinas**, **hábitos** y **cosas que siempre son verdad**.

            Con I / you / we / they el verbo va igual: **I work**, **they live**.

            Con **he / she / it** se agrega **-s**: she work**s**, he live**s**.

            - Terminados en s, sh, ch, x, o: + **es** → watch**es**, go**es**, do**es**
            - Consonante + y: **-ies** → study → stud**ies**
            - **have** → **has**
            """),
           ("La hora", """
            - **What time is it?** → **It's seven o'clock.** (7:00)
            - 7:15 → **It's quarter past seven** / **It's seven fifteen**
            - 7:30 → **It's half past seven** / **It's seven thirty**
            - 7:45 → **It's quarter to eight** / **It's seven forty-five**

            Preposición: **at** 7 o'clock · **in** the morning / afternoon / evening · **at** night
            """)],
          [("I get up at six o'clock.", "Me levanto a las seis."),
           ("She works from home.", "Ella trabaja desde casa."),
           ("He studies at the university.", "Él estudia en la universidad."),
           ("We have breakfast at seven.", "Desayunamos a las siete."),
           ("My brother goes to the gym in the evening.", "Mi hermano va al gimnasio en la noche."),
           ("The class starts at half past eight.", "La clase empieza a las ocho y media."),
           ("I go to bed at eleven.", "Me acuesto a las once."),
           ("It's quarter to nine.", "Son quince para las nueve.")],
          [("get up", "levantarse", "I get up early on weekdays."),
           ("wake up", "despertarse", "She wakes up at 6 a.m."),
           ("have breakfast", "desayunar", "We have breakfast together."),
           ("have lunch", "almorzar", "I have lunch at one."),
           ("have dinner", "cenar", "They have dinner at eight."),
           ("take a shower", "bañarse (ducha)", "He takes a shower every morning."),
           ("go to work", "ir al trabajo", "I go to work by car."),
           ("start", "empezar", "Work starts at nine."),
           ("finish", "terminar", "Class finishes at five."),
           ("go to bed", "acostarse", "I go to bed late."),
           ("early", "temprano", "I wake up early."),
           ("every day", "todos los días", "I study English every day.")],
          [C("She ___ in a bank.", ["work", "works", "working"], 1),
           C("My dad ___ TV every night.", ["watchs", "watches", "watch"], 1, "watch termina en ch → **watches**."),
           C("He ___ two cars.", ["haves", "has", "have"], 1),
           C("Tercera persona de 'study':", ["studys", "studies", "studyes"], 1),
           C("7:30 se dice…", ["half to seven", "half past seven", "seven and half"], 1),
           C("I go to the gym ___ the evening.", ["at", "on", "in"], 2),
           F("My sister ___ (go) to school by bus.", "goes"),
           F("We ___ (live) in Guatemala City.", "live"),
           F("The movie starts ___ 8 o'clock.", "at"),
           T("Él trabaja desde casa.", "He works from home"),
           T("Me levanto a las seis.", "I get up at six|I wake up at six|I get up at 6|I wake up at 6")]),

        U("a1-06", "¿Lo haces seguido?", "Presente simple: negativo, preguntas y frecuencia",
          "Preguntar y negar en presente y decir con qué frecuencia haces algo.",
          [("Negativo con don't / doesn't", """
            - I / you / we / they → **don't** + verbo
            - he / she / it → **doesn't** + verbo **sin -s**

            > I don't like coffee.
            > She doesn't work on Sundays. (no *doesn't works*)
            """),
           ("Preguntas con do / does", """
            > **Do** you speak English? — Yes, I do. / No, I don't.
            > **Does** he play soccer? — Yes, he does. / No, he doesn't.
            > Where **do** you live? · What time **does** the bank open?

            Otra vez: con does el verbo va **sin -s**.
            """),
           ("Adverbios de frecuencia", """
            **always** (100%) → **usually** → **often** → **sometimes** → **rarely** → **never** (0%)

            Van **antes** del verbo principal, pero **después** de to be:

            > I **usually** walk to work.
            > She is **always** late.

            Pregunta: **How often do you…?** → once a week, twice a month, every day.
            """)],
          [("I don't like coffee.", "No me gusta el café."),
           ("She doesn't eat meat.", "Ella no come carne."),
           ("Do you speak English?", "¿Hablas inglés?"),
           ("Where does your brother live?", "¿Dónde vive tu hermano?"),
           ("I usually take the bus.", "Normalmente tomo el bus."),
           ("He is never late.", "Él nunca llega tarde."),
           ("How often do you exercise?", "¿Con qué frecuencia haces ejercicio?"),
           ("We go to the beach twice a year.", "Vamos a la playa dos veces al año.")],
          [("like", "gustar", "I like pizza."),
           ("speak", "hablar (un idioma)", "Do you speak Spanish?"),
           ("eat", "comer", "We eat at home."),
           ("drink", "beber", "I drink a lot of water."),
           ("always", "siempre", "She always smiles."),
           ("usually", "normalmente", "I usually cook dinner."),
           ("often", "a menudo", "We often play video games."),
           ("sometimes", "a veces", "Sometimes I walk to work."),
           ("rarely", "casi nunca", "He rarely watches TV."),
           ("never", "nunca", "I never drink soda."),
           ("once", "una vez", "I call my mom once a week."),
           ("twice", "dos veces", "She runs twice a week.")],
          [C("She ___ like spicy food.", ["don't", "doesn't", "isn't"], 1),
           C("___ you work on Saturdays?", ["Does", "Are", "Do"], 2),
           C("He doesn't ___ English.", ["speaks", "speak", "speaking"], 1, "Después de doesn't: verbo base."),
           C("Elige el orden correcto:", ["I always am tired.", "I am always tired.", "Always I am tired."], 1,
             "Con **be**, el adverbio va después."),
           C("Elige el orden correcto:", ["She usually drinks tea.", "She drinks usually tea.", "Usually she tea drinks."], 0),
           C("¿Qué significa 'rarely'?", ["casi nunca", "raramente siempre", "a veces"], 0),
           F("Where ___ your parents live?", "do"),
           F("My dad ___ (not / play) soccer.", "doesn't play|does not play"),
           F("___ often do you go to the cinema?", "How"),
           T("No me gusta el café.", "I don't like coffee|I do not like coffee"),
           T("¿Ella habla inglés?", "Does she speak English?")]),

        U("a1-07", "Mi casa", "There is / there are y preposiciones de lugar",
          "Describir tu casa y decir dónde están las cosas.",
          [("There is / there are", """
            Significa **hay**:

            - Singular: **There is (There's)** a sofa in the living room.
            - Plural: **There are** two bedrooms.
            - Negativo: **There isn't** a garage. / **There aren't** any chairs.
            - Pregunta: **Is there** a bank near here? — Yes, there is.
            """),
           ("Preposiciones de lugar", """
            - **in** = dentro de → in the box, in the kitchen
            - **on** = sobre (tocando) → on the table, on the wall
            - **under** = debajo de
            - **next to** = al lado de
            - **between** = entre
            - **in front of** = enfrente de · **behind** = detrás de
            - **near** = cerca de

            > The cat is under the bed.
            > The bank is between the pharmacy and the school.
            """)],
          [("There is a big window in my room.", "Hay una ventana grande en mi cuarto."),
           ("There are two bathrooms.", "Hay dos baños."),
           ("Is there a supermarket near here?", "¿Hay un supermercado cerca?"),
           ("There isn't a garage.", "No hay garaje."),
           ("The keys are on the table.", "Las llaves están sobre la mesa."),
           ("The cat is under the bed.", "El gato está debajo de la cama."),
           ("The bank is next to the park.", "El banco está al lado del parque."),
           ("My car is in front of the house.", "Mi carro está enfrente de la casa.")],
          [("house", "casa", "My house is small but nice."),
           ("apartment", "apartamento", "I rent an apartment."),
           ("room", "cuarto / habitación", "My room has a big window."),
           ("bedroom", "dormitorio", "There are three bedrooms."),
           ("kitchen", "cocina", "We eat in the kitchen."),
           ("bathroom", "baño", "The bathroom is upstairs."),
           ("living room", "sala", "The TV is in the living room."),
           ("window", "ventana", "Open the window, please."),
           ("door", "puerta", "Close the door."),
           ("bed", "cama", "My bed is very comfortable."),
           ("wall", "pared", "There's a clock on the wall."),
           ("near", "cerca de", "I live near the university.")],
          [C("___ a park near my house.", ["There are", "There is", "It is"], 1),
           C("___ three chairs in the kitchen.", ["There is", "There are", "Is there"], 1),
           C("___ a bank near here? — Yes, there is.", ["There is", "Are there", "Is there"], 2),
           C("La foto está colgada en la pared: The photo is ___ the wall.", ["in", "on", "under"], 1),
           C("La farmacia está entre el banco y la escuela: The pharmacy is ___ the bank and the school.",
             ["next to", "between", "behind"], 1),
           C("El perro está detrás de la puerta:", ["The dog is in front of the door.", "The dog is behind the door.", "The dog is under the door."], 1),
           F("There ___ any eggs in the fridge. (negativo)", "aren't|are not"),
           F("The milk is ___ the fridge. (dentro)", "in"),
           T("Hay dos baños.", "There are two bathrooms"),
           T("Las llaves están sobre la mesa.", "The keys are on the table")]),

        U("a1-08", "Puedo hacerlo", "Can / can't y el imperativo",
          "Hablar de habilidades, pedir permiso y dar instrucciones.",
          [("Can / can't", """
            **can** + verbo base = poder / saber hacer algo. Es igual para todas las personas (sin -s).

            > I can swim. She can play the guitar.
            > He **can't** (cannot) drive.
            > **Can** you help me? — Yes, I can. / No, I can't.

            También sirve para **pedir** algo o **permiso**: *Can I open the window?*
            """),
           ("Imperativo", """
            Para dar órdenes, instrucciones o consejos se usa el verbo base **sin sujeto**:

            > Open your books. Turn left. Be careful!

            Negativo con **Don't**: *Don't touch that. Don't be late.*

            Para sonar amable agrega **please**: *Please sit down.*
            """)],
          [("I can speak two languages.", "Puedo hablar dos idiomas."),
           ("She can't drive.", "Ella no sabe manejar."),
           ("Can you help me, please?", "¿Me puedes ayudar, por favor?"),
           ("Can I use your phone?", "¿Puedo usar tu teléfono?"),
           ("Open the window, please.", "Abre la ventana, por favor."),
           ("Don't be late.", "No llegues tarde."),
           ("Turn left at the corner.", "Gira a la izquierda en la esquina."),
           ("He can cook very well.", "Él cocina muy bien.")],
          [("can", "poder / saber (hacer)", "I can swim."),
           ("swim", "nadar", "Can you swim?"),
           ("drive", "manejar", "My dad drives a BMW."),
           ("cook", "cocinar", "I can cook pasta."),
           ("dance", "bailar", "She dances very well."),
           ("sing", "cantar", "I can't sing."),
           ("help", "ayudar", "Can you help me?"),
           ("open", "abrir", "Open the door, please."),
           ("close", "cerrar", "Close your eyes."),
           ("turn left / right", "girar a la izquierda / derecha", "Turn right at the bank."),
           ("careful", "cuidadoso", "Be careful with the stairs."),
           ("language", "idioma", "English is a useful language.")],
          [C("She can ___ the piano.", ["plays", "play", "to play"], 1, "Después de can: verbo base."),
           C("___ you ride a bike?", ["Do", "Can", "Are"], 1),
           C("He ___ drive. He's only 12.", ["can't", "doesn't can", "not can"], 0),
           C("Instrucción negativa: no toques eso.", ["Not touch that.", "Don't touch that.", "No touch that."], 1),
           C("Pedir permiso:", ["Can I sit here?", "Do I can sit here?", "I can sit here?"], 0),
           F("___ be late! The class starts at 8. (negativo)", "Don't|Do not"),
           F("I ___ swim very well. (afirmativo)", "can"),
           F("___ the door, please. (cerrar)", "Close|Shut"),
           T("¿Puedes ayudarme?", "Can you help me?"),
           T("No sé cantar.", "I can't sing|I cannot sing")]),

        U("a1-09", "¿Qué estás haciendo?", "Presente continuo",
          "Describir lo que pasa en este momento.",
          [("Forma", """
            **am / is / are** + verbo con **-ing**:

            > I'm working. She's sleeping. They're playing.
            > I'm **not** watching TV.
            > **Are** you listening? — Yes, I am.

            Ortografía del -ing:
            - write → wri**ting** (se quita la e)
            - run → ru**nning**, sit → si**tting** (consonante doble)
            - lie → **lying**
            """),
           ("Presente simple vs continuo", """
            - **Simple**: rutinas → *I work every day.*
            - **Continuo**: ahora mismo / temporal → *I'm working now.* · *I'm living with my cousin this month.*

            Palabras típicas del continuo: **now**, **right now**, **at the moment**, **today**, **Look!**, **Listen!**

            Algunos verbos casi nunca van en continuo: **like, love, want, need, know, understand**. Se dice *I want coffee*, no *I'm wanting*.
            """)],
          [("I'm studying English right now.", "Estoy estudiando inglés ahora mismo."),
           ("She's talking on the phone.", "Ella está hablando por teléfono."),
           ("Look! It's raining.", "¡Mira! Está lloviendo."),
           ("What are you doing?", "¿Qué estás haciendo?"),
           ("They aren't listening.", "Ellos no están escuchando."),
           ("We're watching a movie.", "Estamos viendo una película."),
           ("He's running in the park.", "Él está corriendo en el parque."),
           ("I need help.", "Necesito ayuda.")],
          [("now", "ahora", "I'm busy now."),
           ("at the moment", "en este momento", "She's at work at the moment."),
           ("rain", "llover / lluvia", "It's raining a lot."),
           ("listen", "escuchar", "Listen to me!"),
           ("watch", "mirar / ver (TV, película)", "We're watching a series."),
           ("wait", "esperar", "I'm waiting for the bus."),
           ("look for", "buscar", "I'm looking for my keys."),
           ("run", "correr", "He runs every morning."),
           ("sleep", "dormir", "The baby is sleeping."),
           ("write", "escribir", "I'm writing an email."),
           ("wear", "llevar puesto", "She's wearing a red dress."),
           ("understand", "entender", "I understand you.")],
          [C("Look! The baby ___.", ["sleeps", "is sleeping", "sleep"], 1),
           C("I ___ to work every day.", ["drive", "am driving", "driving"], 0, "Rutina → presente simple."),
           C("-ing de 'run':", ["runing", "running", "runnig"], 1),
           C("-ing de 'write':", ["writeing", "writing", "writting"], 1),
           C("Elige la correcta:", ["I'm wanting a coffee.", "I want a coffee.", "I wanting a coffee."], 1,
             "**want** no se usa en continuo."),
           C("___ are you doing? — I'm cooking.", ["What", "Where", "Who"], 0),
           F("They ___ (play) video games at the moment.", "are playing|'re playing"),
           F("She ___ (not / work) today.", "isn't working|is not working|'s not working"),
           F("___ you listening to me?", "Are"),
           T("Estoy buscando mis llaves.", "I'm looking for my keys|I am looking for my keys"),
           T("¿Qué estás haciendo?", "What are you doing?")]),

        U("a1-10", "En el súper", "Contables, incontables, some/any, much/many",
          "Hablar de comida, cantidades y hacer compras.",
          [("Contables e incontables", """
            - **Contables**: se pueden contar → an apple, two apples
            - **Incontables**: no tienen plural → water, rice, money, bread, information, advice

            No se dice *a water* ni *breads*: se dice **some water**, **a bottle of water**, **a piece of bread**.
            """),
           ("Some / any", """
            - **some**: en afirmativas → *There's some milk.*
            - **any**: en negativas y preguntas → *There isn't any sugar. Do you have any eggs?*
            - Para ofrecer o pedir se usa **some** en preguntas: *Would you like some coffee?*
            """),
           ("Much / many / a lot of", """
            - **How many** + contable plural → *How many eggs do we need?*
            - **How much** + incontable → *How much water do you drink?*
            - **How much is it?** → ¿Cuánto cuesta?
            - **a lot of** sirve para los dos en afirmativo: *a lot of friends, a lot of money*.
            """)],
          [("Can I have some water, please?", "¿Me da agua, por favor?"),
           ("There isn't any milk.", "No hay leche."),
           ("How many eggs do we need?", "¿Cuántos huevos necesitamos?"),
           ("How much is this?", "¿Cuánto cuesta esto?"),
           ("I'd like a piece of cake.", "Quisiera un pedazo de pastel."),
           ("We don't have much time.", "No tenemos mucho tiempo."),
           ("She has a lot of friends.", "Ella tiene muchos amigos."),
           ("Would you like some coffee?", "¿Quieres café?")],
          [("water", "agua", "I drink a lot of water."),
           ("bread", "pan", "We need some bread."),
           ("rice", "arroz", "Rice and beans, please."),
           ("beans", "frijoles", "I love black beans."),
           ("egg", "huevo", "Two eggs, please."),
           ("milk", "leche", "Is there any milk?"),
           ("chicken", "pollo", "I'd like chicken with rice."),
           ("fruit", "fruta", "Eat more fruit."),
           ("vegetables", "verduras", "Vegetables are healthy."),
           ("money", "dinero", "I don't have much money."),
           ("bottle", "botella", "A bottle of water, please."),
           ("price", "precio", "What's the price of this?")],
          [C("Can I have ___ water?", ["a", "some", "many"], 1),
           C("There aren't ___ apples.", ["some", "any", "much"], 1),
           C("How ___ people are there?", ["much", "many", "lot"], 1),
           C("How ___ money do you have?", ["many", "much", "lots"], 1),
           C("¿Cuál es incontable?", ["egg", "rice", "apple"], 1),
           C("Elige la correcta:", ["I need an advice.", "I need some advice.", "I need advices."], 1,
             "**advice** es incontable."),
           F("Do you have ___ brothers? (pregunta)", "any"),
           F("How ___ is this shirt? — It's 50 quetzales.", "much"),
           T("No hay leche.", "There isn't any milk|There is no milk|There's no milk"),
           T("¿Cuántos huevos necesitamos?", "How many eggs do we need?")]),
    ],
}
