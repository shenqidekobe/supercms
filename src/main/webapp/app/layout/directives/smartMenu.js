define(['layout/module', 'jquery'], function (module) {

    "use strict";

    (function ($) {

        $.fn.smartCollapseToggle = function () {

            return this.each(function () {

                var $body = $('body');
                var $this = $(this);

                // only if not  'menu-on-top'
                if ($body.hasClass('menu-on-top')) {


                } else {

                    $body.hasClass('mobile-view-activated')

                    // toggle open
                    $this.toggleClass('open');

                    // for minified menu collapse only second level
                    if ($body.hasClass('minified')) {
                        if ($this.closest('nav ul ul').length) {
                            $this.find('>a .collapse-sign .fa').toggleClass('fa-minus-square-o fa-plus-square-o');
                            $this.find('ul:first').slideToggle(200);
                        }
                    } else {
                        // toggle expand item
                        $this.find('>a .collapse-sign .fa').toggleClass('fa-minus-square-o fa-plus-square-o');
                        $this.find('ul:first').slideToggle(200);
                    }
                }
            });
        };
    })(jQuery);

    module.registerDirective('smartMenu', function ($state, $rootScope, User, $compile) {
        return {
            restrict: 'A',
            compile: function(element, attrs){
            	return function (scope, element, attrs) {
        		    User.initialized.then(function(){
        		    	var menuAsHtml = '';
        		    	var getMenuAsHtml = function(menus){
        		    		var partial = '';
        			    	$(menus).each(function(idx, menu){
        		    			if(menu.children && menu.children.length == 0) {
        		    				if(menu.lvl == 1){
        		    					partial += _.template('<li data-ui-sref-active="active"><a data-ui-sref="<%=uisref%>" ui-sref-opts="{reload: false}" title="<%=title%>"><i class="<%=icon%>"></i><span class="menu-item-parent"><%=title%></span></a></li>')(menu);
        		    				}else {
        		    					partial += _.template('<li data-ui-sref-active="active"><a data-ui-sref="<%=uisref%>" ui-sref-opts="{reload: false}"><i class="<%=icon%>"></i><%=title%></a></li>')(menu);
        		    				}
        		    			}else{
        		    				partial += '<li data-menu-collapse>';
        		    				partial += _.template('<a href="#"><i class="fa fa-lg fa-fw <%=icon%>"></i><span class="menu-item-parent"><%=title%></span></a>')(menu);
        		    				partial += '<ul>';
        		    				partial += getMenuAsHtml(menu.children);
        		    				partial += '</ul>';
        		    				partial += '</li>';
        		    			}
        			    	});
        			    	return partial;
        			    };
        		    	menuAsHtml += getMenuAsHtml(User.menus);
        		    	var menuAsElem = angular.element(menuAsHtml);
        		    	$compile(menuAsElem)(scope);
        		    	element.append(menuAsElem);
        		    	
        		    	var $body = $('body');

                        var $collapsible = element.find('li[data-menu-collapse]');
                        $collapsible.each(function (idx, li) {
                            var $li = $(li);
                            $li.on('click', '>a', function (e) {
                                    // collapse all open siblings
                                    $li.siblings('.open').smartCollapseToggle();

                                    // toggle element
                                    $li.smartCollapseToggle();

                                    // add active marker to collapsed element if it has active childs
                                    if (!$li.hasClass('open') && $li.find('li.active').length > 0) {
                                        $li.addClass('active')
                                    }

                                    e.preventDefault();
                                }).find('>a').append('<b class="collapse-sign"><em class="fa fa-plus-square-o"></em></b>');

                            // initialization toggle
                            if ($li.find('li.active').length) {
                                $li.smartCollapseToggle();
                                $li.find('li.active').parents('li').addClass('active');
                            }
                        });

                        // click on route link
                        element.on('click', 'a[data-ui-sref]', function (e) {
                            // collapse all siblings to element parents and remove active markers
                            $(this)
                                .parents('li').addClass('active')
                                .each(function () {
                                    $(this).siblings('li.open').smartCollapseToggle();
                                    $(this).siblings('li').removeClass('active')
                                });

                            if ($body.hasClass('mobile-view-activated')) {
                                $rootScope.$broadcast('requestToggleMenu');
                            }
                        });


                        scope.$on('$smartLayoutMenuOnTop', function (event, menuOnTop) {
                            if (menuOnTop) {
                                $collapsible.filter('.open').smartCollapseToggle();
                            }
                        });
        		    });
            	}
            }
        }
    });


});
