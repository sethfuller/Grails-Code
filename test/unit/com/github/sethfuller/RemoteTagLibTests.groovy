
package com.github.sethfuller

/*
 * This is in the grails-test-suite-base project
 */
import org.codehaus.groovy.grails.web.taglib.AbstractGrailsTagTests
/*
 * 
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Tests for the RemoteTagLib.groovy file which contains tags that provide remote
 * versions of paginate (paginateRemote) and sortableColumn (sortableColumnRemote)
 *
 * @author Seth Fuller
 */
class RemoteTagLibTests extends AbstractGrailsTagTests {

    void testPaginateRemoteTag() {
         def template = '<g:paginateRemote controller="book" total="" offset="" />'
         applyTemplate(template)
     }

    void testPaginateRemoteOmissionAttributes() {
        def template = '<g:paginateRemote next="Forward" prev="Backward" max="5" total="20" offset="10" controller="book" action="list"/>'
        assertOutputEquals '<a href="/book/list?offset=5&amp;max=5" class="prevLink">Backward</a><a href="/book/list?offset=0&amp;max=5" class="step">1</a><a href="/book/list?offset=5&amp;max=5" class="step">2</a><span class="currentStep">3</span><a href="/book/list?offset=15&amp;max=5" class="step">4</a><a href="/book/list?offset=15&amp;max=5" class="nextLink">Forward</a>', template

        template = '<g:paginateRemote next="Forward" prev="Backward" max="5" total="20" offset="10" controller="book" action="list" omitPrev="true"/>'
        assertOutputNotContains 'Backward', template
        assertOutputContains 'Forward', template

        template = '<g:paginateRemote next="Forward" prev="Backward" max="5" total="20" offset="10" controller="book" action="list" omitPrev="false"/>'
        assertOutputContains 'Backward', template
        assertOutputContains 'Forward', template

        template = '<g:paginateRemote next="Forward" prev="Backward" max="5" total="20" offset="10" controller="book" action="list" omitNext="true"/>'
        assertOutputContains 'Backward', template
        assertOutputNotContains 'Forward', template

        template = '<g:paginateRemote next="Forward" prev="Backward" max="5" total="20" offset="10" controller="book" action="list" omitNext="false"/>'
        assertOutputContains 'Backward', template
        assertOutputContains 'Forward', template

        template = '<g:paginateRemote max="2" total="20" offset="10" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitFirst="true" />'
        assertOutputNotContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a>', template
        assertOutputContains '<a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="2" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitFirst="true" />'
        assertOutputContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="10" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitFirst="false" />'
        assertOutputContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="10" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitLast="true" />'
        assertOutputContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a>', template
        assertOutputNotContains '<a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="16" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitLast="true" />'
        assertOutputContains '<a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="10" maxsteps="3" controller="book" action="list" omitPrev="true" omitNext="true" omitLast="false" />'
        assertOutputContains '<a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template
    }

    void testPaginateRemoteGap() {
        def template = '<g:paginateRemote max="2" total="20" offset="10" maxsteps="3" controller="book" action="list" />'
        assertOutputContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a><span class="step gap">..</span><a href="/book/list?offset=8&amp;max=2" class="step">5</a>', template
        assertOutputContains '<a href="/book/list?offset=12&amp;max=2" class="step">7</a><span class="step gap">..</span><a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="4" maxsteps="3" controller="book" action="list" />'
        assertOutputContains '<a href="/book/list?offset=0&amp;max=2" class="step">1</a><a href="/book/list?offset=2&amp;max=2" class="step">2</a>', template

        template = '<g:paginateRemote max="2" total="20" offset="14" maxsteps="3" controller="book" action="list" />'
        assertOutputContains '<a href="/book/list?offset=16&amp;max=2" class="step">9</a><a href="/book/list?offset=18&amp;max=2" class="step">10</a>', template
    }

    protected void onInit() {
        if(name == 'testPaginateMappingAndAction') {
            def mappingClass = gcl.parseClass('''
    class TestUrlMappings {
        static mappings = {
            name claimTab: "/claim/$id/$action" {
                controller = 'Claim'
                constraints { id(matches: /\\d+/) }
            }
        }
    }
            ''')

            grailsApplication.addArtefact(UrlMappingsArtefactHandler.TYPE, mappingClass)
        }
    }

    void testPaginateRemoteMappingAndAction() {
        def template = '<g:paginateRemote next="Forward" prev="Back" maxsteps="8" max="10" id="1" mapping="claimTab" total="12" action="documents"/>'
        assertOutputEquals '<span class="currentStep">1</span><a href="/claim/1/documents?offset=10&amp;max=10" class="step">2</a><a href="/claim/1/documents?offset=10&amp;max=10" class="nextLink">Forward</a>', template
    }


    void testSortableColumnRemoteTag() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }
    }

    void testSortableColumnRemoteTagWithTitleKey() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        // test message not resolved; title property will be used (when provided)

        // without (default) title property provided
        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", titleKey:"book.title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'book.title')
        }

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        // with (default) title property provided
        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title", titleKey:"book.title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }

        // test message resolved

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        messageSource.addMessage("book.title", RCU.getLocale(request), "Book Title")

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title", titleKey:"book.title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Book Title')
        }
    }

    void testSortableColumnRemoteTagWithAction() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([action:"list2", property:"title", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }
    }

    void testSortableColumnRemoteTagWithDefaultOrder() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        // default order: desc

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", defaultOrder:"desc", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'desc', 'Title')
        }

        // default order: asc

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", defaultOrder:"asc", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }

        // invalid default order

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", defaultOrder:"invalid", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }
    }

    void testSortableColumnRemoteTagWithAdditionalAttributes() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // use sorted map to be able to predict the order in which tag attributes are generated
            // adding the class property is a dirty hack to predict the order; it will be overridden in the tag anyway
            def attrs = new TreeMap([property:"title", title:"Title", class:"other", style:"width: 200px;"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'other sortable', 'asc', 'Title', ' style="width: 200px;"')
        }
    }

    void testSortableColumnRemoteTagSorted() {
        final StringWriter sw = new StringWriter()
        final PrintWriter pw = new PrintWriter(sw)

        // column sorted asc

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // set request params
            webRequest.getParams().put("sort", "title")
            webRequest.getParams().put("order", "asc")
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable sorted asc', 'desc', 'Title')
        }

        // column sorted desc

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // set request params
            webRequest.getParams().put("sort", "title")
            webRequest.getParams().put("order", "desc")
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable sorted desc', 'asc', 'Title')
        }

        // other column sorted

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // set request params
            webRequest.getParams().put("sort", "price")
            webRequest.getParams().put("order", "desc")
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title"])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }

        // sort in params attribute

        sw = new StringWriter()
        pw = new PrintWriter(sw)

        withTag("sortableColumnRemote", pw) { tag ->
            webRequest.controllerName = "book"
            // set request params
            webRequest.getParams().put("sort", "price")
            webRequest.getParams().put("order", "desc")
            // use sorted map to be able to predict the order in which tag attributes are generated
            def attrs = new TreeMap([property:"title", title:"Title", params:[sort:"id"]])
            tag.call(attrs)

            checkTagOutput(sw.toString(), 'sortable', 'asc', 'Title')
        }
    }

    /**
     * Checks that the given output matches what is expected from the
     * tag, based on the given parameters. It ensures that the order
     * of the query parameters in the generated anchor's 'href' attribute
     * is not significant. If the output does not match the expected
     * text, an assertion is thrown.
     * @param output The output to check (String).
     * @param expectedClassValue The expected contents of the 'class'
     * attribute in the tag's output (String).
     * @param expectedOrder The expected sort order generated by the
     * tag (either 'asc' or 'desc').
     * @param expectedContent The expected content of the generated
     * anchor tag (String).
     */
    void checkTagOutput(output, expectedClassValue, expectedOrder, expectedContent) {
        // Check the output of the tag. The query parameters are not
        // guaranteed to be in any particular order, so we extract
        // them with a regular expression.
        def p = ~"<th class=\"${expectedClassValue}\" ><a href=\"\\S+?(\\w+=\\w+)&amp;(\\w+=\\w+)\">${expectedContent}</a></th>"
        def m = p.matcher(output)

        // First step: check the output as a whole matches what we
        // expect.
        assertTrue "Output [$output] doesn't match expected pattern", m.matches()

        // Now make sure the expected query parameters are there,
        // regardless of their order.
        if (m.group(1) == 'sort=title') {
            assertEquals m.group(2), "order=${expectedOrder}"
        }
        else {
            assertEquals m.group(1), "order=${expectedOrder}"
            assertEquals m.group(2), 'sort=title'
        }
    }
}
