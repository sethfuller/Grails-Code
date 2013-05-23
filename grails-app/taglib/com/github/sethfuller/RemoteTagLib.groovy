package com.github.sethfuller

import org.springframework.web.servlet.support.RequestContextUtils as RCU

class RemoteTagLib {
	
    /**
     * Renders a sortable column using a remote ajax link to support sorting in list views.<br/>
     *
     * Attribute title or titleKey is required. When both attributes are specified then titleKey takes precedence,
     * resulting in the title caption to be resolved against the message source. In case when the message could
     * not be resolved, the title will be used as title caption.<br/>
     *
     * Examples:<br/>
     *
     * &lt;g:sortableColumnRemote property="title" update="contentDiv" title="Title" /&gt;<br/>
     * &lt;g:sortableColumnRemote property="title" update="contentDiv" title="Title" style="width: 200px" /&gt;<br/>
     * &lt;g:sortableColumnRemote property="title" update="contentDiv" titleKey="book.title" /&gt;<br/>
     * &lt;g:sortableColumnRemote property="releaseDate" update="contentDiv" defaultOrder="desc" title="Release Date" /&gt;<br/>
     * &lt;g:sortableColumnRemote property="releaseDate" update="contentDiv" defaultOrder="desc" title="Release Date" titleKey="book.releaseDate" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr property - name of the property relating to the field
     * @attr defaultOrder default order for the property; choose between asc (default if not provided) and desc
     * @attr title title caption for the column
     * @attr titleKey title key to use for the column, resolved against the message source
     * @attr params a map containing request parameters
     * @attr action the name of the action to use in the link, if not specified the list action will be linked
     * @attr params A map containing URL query parameters
     * @attr class CSS class name
     * @attr update Either a map containing the elements to update for 'success' or 'failure' states, or a string with the element to update in which cause failure events would be ignored
     * @attr before The javascript function to call before the remote function call
     * @attr after The javascript function to call after the remote function call
     * @attr asynchronous Whether to do the call asynchronously or not (defaults to true)
     * @attr method The method to use the execute the call (defaults to "post")
     */
	Closure sortableColumnRemote = { attrs ->
		def writer = out
		if (!attrs.property) {
			throwTagError("Tag [sortableColumnRemote] is missing required attribute [property]")
		}

		if (!attrs.title && !attrs.titleKey) {
			throwTagError("Tag [sortableColumnRemote] is missing required attribute [title] or [titleKey]")
		}

		def property = attrs.remove("property")
		def action = attrs.action ? attrs.remove("action") : (actionName ?: "list")

		def defaultOrder = attrs.remove("defaultOrder")
		if (defaultOrder != "desc") defaultOrder = "asc"

		// current sorting property and order
		def sort = params.sort
		def order = params.order

		// add sorting property and params to link params
		def linkParams = [:]
		if (params.id) linkParams.put("id", params.id)
		def paramsAttr = attrs.remove("params")
		if (paramsAttr) linkParams.putAll(paramsAttr)
		linkParams.sort = property

		// propagate "max" and "offset" standard params
		if (params.max) linkParams.max = params.max
		if (params.offset) linkParams.offset = params.offset

		// determine and add sorting order for this column to link params
		attrs.class = (attrs.class ? "${attrs.class} sortable" : "sortable")
		if (property == sort) {
			attrs.class = attrs.class + " sorted " + order
			if (order == "asc") {
				linkParams.order = "desc"
			}
			else {
				linkParams.order = "asc"
			}
		}
		else {
			linkParams.order = defaultOrder
		}

		// determine column title
		def title = attrs.remove("title")
		def titleKey = attrs.remove("titleKey")
		def mapping = attrs.remove('mapping')
		if (titleKey) {
			if (!title) title = titleKey
			def messageSource = grailsAttributes.messageSource
			def locale = RCU.getLocale(request)
			title = messageSource.getMessage(titleKey, null, title, locale)
		}

		writer << "<th "
		// process remaining attributes
		attrs.each { k, v ->
			writer << "${k}=\"${v?.encodeAsHTML()}\" "
		}
		writer << '>'
		def linkAttrs = [params: linkParams]
		if(mapping) {
			linkAttrs.mapping = mapping
		} else {
			linkAttrs.action = action
		}
		if (attrs.update) {
			linkAttrs.update = attrs.remove("update")
		}
		if (attrs.before) {
			linkAttrs.before = attrs.remove("before")
		}
		if (attrs.after) {
			linkAttrs.after = attrs.remove("after")
		}
		if (attrs.asynchronous) {
			linkAttrs.asynchronous = attrs.remove("asynchronous")
		}
		if (attrs.method) {
			linkAttrs.method = attrs.remove("method")
		}
		if (attrs.onSuccess) {
			linkAttrs.onSuccess = attrs.remove("onSuccess")
		}
		if (attrs.onFailure) {
			linkAttrs.onFailure = attrs.remove("onFailure")
		}
		if (attrs.on_ERROR_CODE) {
			linkAttrs.on_ERROR_CODE = attrs.remove("on_ERROR_CODE")
		}
		if (attrs.onUnitialized) {
			linkAttrs.onUnitialized = attrs.remove("onUnitialized")
		}
		if (attrs.onLoading) {
			linkAttrs.onLoading = attrs.remove("onLoading")
		}
		if (attrs.onLoaded) {
			linkAttrs.onLoaded = attrs.remove("onLoaded")
		}
		if (attrs.onComplete) {
			linkAttrs.onComplete = attrs.remove("onComplete")
		}

		writer << remoteLink(linkAttrs) {
			title
		}
		writer << '</th>'
	}

    /**
     * Creates next/previous links to support pagination for the current controller.<br/>
     * This tag uses remoteLink to allow updating for instance a tab.
     *
     * &lt;g:paginateRemote total="${Account.count()}" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr total REQUIRED The total number of results to paginate
     * @attr action the name of the action to use in the link, if not specified the default action will be linked
     * @attr controller the name of the controller to use in the link, if not specified the current controller will be linked
     * @attr id The id to use in the link
     * @attr params A map containing request parameters
     * @attr prev The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties)
     * @attr next The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties)
     * @attr omitPrev Whether to not show the previous link (if set to true, the previous link will not be shown)
     * @attr omitNext Whether to not show the next link (if set to true, the next link will not be shown)
     * @attr omitFirst Whether to not show the first link (if set to true, the first link will not be shown)
     * @attr omitLast Whether to not show the last link (if set to true, the last link will not be shown)
     * @attr max The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
     * @attr maxsteps The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxsteps is empty
     * @attr offset Used only if params.offset is empty
     * @attr mapping The named URL mapping to use to rewrite the link
     * @attr fragment The link fragment (often called anchor tag) to use
     */
    Closure paginateRemote = { attrs ->
        def writer = out
        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }

        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)

        def total = attrs.int('total') ?: 0
        def offset = params.int('offset') ?: 0
        def max = params.int('max')
        def maxsteps = (attrs.int('maxsteps') ?: 10)

        if (!offset) offset = (attrs.int('offset') ?: 0)
        if (!max) max = (attrs.int('max') ?: 10)

        def linkParams = [:]
        if (attrs.params) linkParams.putAll(attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        def linkTagAttrs = [:]
        def action
        if(attrs.containsKey('mapping')) {
            linkTagAttrs.mapping = attrs.mapping
            action = attrs.action
        } else {
            action = attrs.action ?: params.action
        }
        if(action) {
            linkTagAttrs.action = action
        }
            if (attrs.controller) {
                linkTagAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.fragment != null) {
            linkTagAttrs.fragment = attrs.fragment
        }
	if (attrs.update) {
	  linkTagAttrs.update = attrs.remove("update")
	}
	if (attrs.before) {
	  linkTagAttrs.before = attrs.remove("before")
	}
	if (attrs.after) {
	  linkTagAttrs.after = attrs.remove("after")
	}
	if (attrs.asynchronous) {
	  linkTagAttrs.asynchronous = attrs.remove("asynchronous")
	}
	if (attrs.method) {
	  linkTagAttrs.method = attrs.remove("method")
	}
	if (attrs.onSuccess) {
	  linkTagAttrs.onSuccess = attrs.remove("onSuccess")
	}
	if (attrs.onFailure) {
	  linkTagAttrs.onFailure = attrs.remove("onFailure")
	}
	if (attrs.on_ERROR_CODE) {
	  linkTagAttrs.on_ERROR_CODE = attrs.remove("on_ERROR_CODE")
	}
	if (attrs.onUnitialized) {
	  linkTagAttrs.onUnitialized = attrs.remove("onUnitialized")
	}
	if (attrs.onLoading) {
	  linkTagAttrs.onLoading = attrs.remove("onLoading")
	}
	if (attrs.onLoaded) {
	  linkTagAttrs.onLoaded = attrs.remove("onLoaded")
	}
	if (attrs.onComplete) {
	  linkTagAttrs.onComplete = attrs.remove("onComplete")
	}
        linkTagAttrs.params = linkParams

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = (offset / max) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil(total / max))

        // display previous link when not on firststep unless omitPrev is true
        if (currentstep > firststep && !attrs.boolean('omitPrev')) {
            linkTagAttrs.class = 'prevLink'
            linkParams.offset = offset - max
            writer << remoteLink(linkTagAttrs.clone()) {
                (attrs.prev ?: messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            }
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // determine begin and endstep paging variables
            int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            int endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep && !attrs.boolean('omitFirst')) {
                linkParams.offset = 0
                writer << remoteLink(linkTagAttrs.clone()) {firststep.toString()}
            }
            //show a gap if beginstep isn't immediately after firststep, and if were not omitting first or rev
            if (beginstep > firststep+1 && (!attrs.boolean('omitFirst') || !attrs.boolean('omitPrev')) ) {
                writer << '<span class="step gap">..</span>'
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << "<span class=\"currentStep\">${i}</span>"
                }
                else {
                    linkParams.offset = (i - 1) * max
                    writer << remoteLink(linkTagAttrs.clone()) {i.toString()}
                }
            }

            //show a gap if beginstep isn't immediately before firststep, and if were not omitting first or rev
            if (endstep+1 < laststep && (!attrs.boolean('omitLast') || !attrs.boolean('omitNext'))) {
                writer << '<span class="step gap">..</span>'
            }
            // display laststep link when endstep is not laststep
            if (endstep < laststep && !attrs.boolean('omitLast')) {
                linkParams.offset = (laststep - 1) * max
                writer << remoteLink(linkTagAttrs.clone()) { laststep.toString() }
            }
        }

        // display next link when not on laststep unless omitNext is true
        if (currentstep < laststep && !attrs.boolean('omitNext')) {
            linkTagAttrs.class = 'nextLink'
            linkParams.offset = offset + max
            writer << remoteLink(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            }
        }
    }


}
