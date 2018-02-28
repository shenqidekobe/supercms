define([
    // account auth
    'auth/module',
    'auth/models/User',

    // layout
    'layout/module',
    'layout/actions/minifyMenu',
    'layout/actions/toggleMenu',
    'layout/actions/fullScreen',
    'layout/actions/resetWidgets',
    'layout/actions/resetWidgets',
    'layout/directives/demo/demoStates',
    'layout/directives/smartInclude',
    'layout/directives/smartDeviceDetect',
    'layout/directives/smartFastClick',
    'layout/directives/smartLayout',
    'layout/directives/smartRouterAnimationWrap',
    'layout/directives/smartFitAppView',
    'layout/directives/radioToggle',
    'layout/directives/dismisser',
    'layout/directives/smartMenu', //为了生成动态菜单，修改了smaradmin自带的smartmenu指令
    'layout/directives/bigBreadcrumbs',
    'layout/directives/stateBreadcrumbs',
    'layout/directives/smartPageTitle',
    'layout/directives/hrefVoid',
    'layout/service/SmartCss',
    'modules/widgets/directives/widgetGrid',
    'modules/widgets/directives/jarvisWidget',
    // shortcut
    'components/shortcut/shortcut-directive',
    // forms
    'modules/forms/module',
    // ui
    'modules/ui/module',
    // tables
    'modules/tables/module',
    // misc
    'modules/misc/module',

    // 应用服务
    'modules/services/module',
    
    // 控制台
    'dashboard/module',
    
    // 模块
    'modules/modules/module',
    
    // 模版
    'modules/templates/module',
    
    //数据
    'modules/datas/module',
    
    //系统设置
    'modules/settings/module',
    
    //投稿设置
    'modules/manuscripts/module',
    
    // 网盘
    'filemanager/module',
    
    // 安全中心
    'modules/security/module'
    
], function () {
    'use strict';
});
