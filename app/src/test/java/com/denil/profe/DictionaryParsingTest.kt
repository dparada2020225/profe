package com.denil.profe

import com.denil.profe.data.remote.DictionaryClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Formatos reales de las APIs (verificados con el workflow "Probar APIs"). */
class DictionaryParsingTest {
    private val client = DictionaryClient()

    @Test
    fun `tatoeba devuelve pares ingles-espanol`() {
        val body = """{"paging":{},"results":[{"id":1,"text":"Achieve your dreams.","lang":"eng",
            "translations":[[{"id":2,"text":"Alcanza tus sueños.","lang":"spa","isDirect":true}],[]]},
            {"id":3,"text":"No translation here.","lang":"eng","translations":[[],[]]}]}"""
        val pairs = client.parseTatoeba(body, 5)
        assertEquals(1, pairs.size)
        assertEquals("Achieve your dreams.", pairs[0].en)
        assertEquals("Alcanza tus sueños.", pairs[0].es)
    }

    @Test
    fun `wiktionary limpia el html`() {
        val body = """{"en":[{"partOfSpeech":"Verb","language":"English","definitions":[
            {"definition":"<span class=\"x\"></span>To <a href=\"/wiki/carry_out\">carry out</a> &amp; finish","examples":["<i>She achieved</i> it."]},
            {"definition":"","examples":[]}]}]}"""
        val entry = client.parseWiktionary(body, "achieve")
        assertNotNull(entry)
        assertEquals("To carry out & finish", entry!!.definitions[0].definition)
        assertEquals("She achieved it.", entry.definitions[0].example)
        assertEquals(1, entry.definitions.size)
    }

    @Test
    fun `free dictionary toma fonetica y audio`() {
        val body = """[{"word":"hello","phonetic":"həˈləʊ","phonetics":[{"text":"həˈləʊ","audio":"//ssl.gstatic.com/hello-uk.mp3"},
            {"text":"həˈloʊ","audio":"https://x/hello-us.mp3"}],"meanings":[{"partOfSpeech":"noun","definitions":[{"definition":"A greeting.","example":"Hello!"}],"synonyms":["hi"]}]}]"""
        val e = client.parseFreeDictionary(body, "hello")!!
        assertEquals("https://x/hello-us.mp3", e.audioUrl)
        assertTrue(e.synonyms.contains("hi"))
        assertEquals("A greeting.", e.definitions[0].definition)
    }
}
