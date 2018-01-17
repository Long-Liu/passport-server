/**
 * Using state to manage routing and views
 * Each view are defined as state.
 * Initial there are written state for all view in theme.
 *
 */
angular
    .module('smartcloudserviceApp')
    .config(stateConfig)
    .config(paginationConfig)
    .config(pagerConfig)
    .config(httpConfig)
    .config(localStorageConfig)
    .config(compileServiceConfig)
    .run(function ($rootScope, $state) {
        $rootScope.$state = $state;
    });

function stateConfig($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, IdleProvider, KeepaliveProvider, APP_NAME) {

    // Configure Idle settings
    IdleProvider.idle(5); // in seconds
    IdleProvider.timeout(120); // in seconds

    $urlRouterProvider.otherwise("/");

    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider
        .state('layout', {
            abstract: true,
            templateUrl: "app/views/common/layout.html"
        })
        .state('dashboard', {
            parent: 'layout',
            url: "/",
            views: {
                'content@': {
                    templateUrl: 'app/views/common/dashboard.html'
                }
            },
            data: {
                pageTitle: 'Dashboard',
                authorities: ['ROLE_ADMIN', 'ROLE_DEVELOPER', 'ROLE_USER']
            },
            resolve: {
                loadPlugin: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        {
                            serie: true,
                            name: 'angular-flot',
                            files: ['content/js/plugins/flot/jquery.flot.js', 'content/js/plugins/flot/jquery.flot.time.js', 'content/js/plugins/flot/jquery.flot.tooltip.min.js', 'content/js/plugins/flot/jquery.flot.spline.js', 'content/js/plugins/flot/jquery.flot.resize.js', 'content/js/plugins/flot/jquery.flot.pie.js', 'content/js/plugins/flot/curvedLines.js', 'content/js/plugins/flot/angular-flot.js',]
                        },
                        {
                            name: 'angular-peity',
                            files: ['content/js/plugins/peity/jquery.peity.min.js', 'content/js/plugins/peity/angular-peity.js']
                        }
                    ]);
                }
            }
        })
        .state('error', {
            parent: 'layout',
            url: '/error',
            views: {
                'content@': {
                    templateUrl: 'app/views/common/error.html',
                    controller: 'ErrorPageController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '错误页面',
                authorities: []
            },
            params: {
                errorMessage: ''
            }
        })
        .state('accessdenied', {
            parent: 'layout',
            url: '/accessdenied',
            views: {
                'content@': {
                    templateUrl: 'app/views/common/accessdenied.html'
                }
            },
            data: {
                pageTitle: '访问拒绝',
                authorities: []
            }
        })
        .state('login', {
            url: "/login",
            templateUrl: 'app/views/common/login.html',
            controller: 'LoginController',
            controllerAs: 'vm',
            data: {
                pageTitle: '登录',
                specialClass: 'gray-bg login-background-img'
            }
        })
        .state('register', {
            url: "/register",
            templateUrl: 'app/views/common/register.html',
            controller: 'RegisterController',
            controllerAs: 'vm',
            data: {
                pageTitle: '注册',
                specialClass: 'gray-bg register-background-img'
            }
        })
        .state('activate', {
            url: '/activate?key',
            templateUrl: 'app/views/common/activate.html',
            controller: 'ActivationController',
            controllerAs: 'vm',
            data: {
                pageTitle: '激活账号',
                specialClass: 'gray-bg activate-background-img'
            }
        })
        .state('forgot-password', {
            url: "/forgot-password",
            templateUrl: 'app/views/common/forgot-password.html',
            controller: 'ForgotPasswordController',
            controllerAs: 'vm',
            data: {
                pageTitle: '发送密码重置邮件',
                specialClass: 'gray-bg forget-password-background-img'
            }
        })
        .state('reset-password', {
            url: "/reset-password",
            templateUrl: 'app/views/common/reset-password.html',
            controller: 'ResetPasswordController',
            controllerAs: 'vm',
            data: {
                pageTitle: '重置密码',
                specialClass: 'gray-bg'
            }
        })
        .state('user', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_USER']
            }
        })
        .state('profile', {
            parent: 'user',
            url: '/profile',
            views: {
                'content@': {
                    templateUrl: 'app/views/user/profile/profile.html',
                    controller: 'ProfileController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '个人信息'
            }
        })
        .state('password', {
            parent: 'user',
            url: '/password',
            views: {
                'content@': {
                    templateUrl: 'app/views/user/password/password.html',
                    controller: 'PasswordController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '修改密码'
            }
        })
        .state('developer', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_DEVELOPER']
            }
        })
        .state('api', {
            parent: 'developer',
            url: '/api',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/api/api.html'
                }
            },
            data: {
                pageTitle: 'API'
            }
        })
        .state('api-docs', {
            parent: 'developer',
            url: '/api-docs',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/api-docs/api-docs.html'
                }
            },
            data: {
                pageTitle: 'API Docs'
            }
        })
        .state('metrics', {
            parent: 'developer',
            url: '/metrics',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/metrics/metrics.html',
                    controller: 'MetricsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Metrics'
            },
            resolve: {
                metrics: ['MetricsService', function (MetricsService) {
                    return MetricsService.getMetrics();
                }]
            }
        })
        .state('health', {
            parent: 'developer',
            url: '/health',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/health/health.html',
                    controller: 'HealthController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Health'
            }
        })
        .state('configuration', {
            parent: 'developer',
            url: '/configuration',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/configuration/configuration.html',
                    controller: 'ConfigurationController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Configuration'
            }
        })
        .state('beans', {
            parent: 'developer',
            url: '/beans',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/beans/beans.html',
                    controller: 'BeansController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Beans'
            }
        })
        .state('mappings', {
            parent: 'developer',
            url: '/mappings',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/mappings/mappings.html',
                    controller: 'MappingsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Mappings'
            }
        })
        .state('trace', {
            parent: 'developer',
            url: '/trace',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/trace/trace.html',
                    controller: 'TraceController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Trace'
            }
        })
        .state('audits', {
            parent: 'developer',
            url: '/audits',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/audits/audits.html',
                    controller: 'AuditsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Audits'
            }
        })
        .state('dict-list', {
            parent: 'developer',
            url: '/dict-list?page&sort&dictName',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/dict/dict-list.html',
                    controller: 'DictListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '数据字典列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'dictCode,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        dictName: $stateParams.dictName
                    };
                }]
            }
        })
        .state('dict-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建数据字典信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            dictCode: null,
                            dictName: null,
                            remark: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-list.edit', {
            url: '/edit/{id}',
            data: {
                pageTitle: '编辑数据字典信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict/dict-dialog.html',
                    controller: 'DictDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DictService', function (DictService) {
                            return DictService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-item-list', {
            parent: 'developer',
            url: '/dict-item-list?page&sort&dictCode&dictItemName',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/dict-item/dict-item-list.html',
                    controller: 'DictItemListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '数据字典项列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'dictCode,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        dictCode: $stateParams.dictCode,
                        dictItemName: $stateParams.dictItemName
                    };
                }]
            }
        })
        .state('dict-item-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建数据字典项信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            dictItemCode: null,
                            dictItemName: null,
                            dictCode: null,
                            remark: null,
                            enabled: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('dict-item-list.edit', {
            url: '/edit/{id}',
            data: {
                pageTitle: '编辑数据字典项信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/developer/dict-item/dict-item-dialog.html',
                    controller: 'DictItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['DictItemService', function (DictItemService) {
                            return DictItemService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('logs', {
            parent: 'developer',
            url: '/logs',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/logs/logs.html',
                    controller: 'LogsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Logs Level'
            }
        })
        .state('redis-admin', {
            parent: 'developer',
            url: '/redis-admin',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/redis-admin/redis-admin.html',
                    controller: 'RedisAdminController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Redis Admin'
            }
        })
        .state('control', {
            parent: 'developer',
            url: '/control',
            views: {
                'content@': {
                    templateUrl: 'app/views/developer/control/control.html',
                    controller: 'ControlController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: 'Control'
            }
        })
        .state('admin', {
            abstract: true,
            parent: 'layout',
            data: {
                authorities: ['ROLE_ADMIN']
            }
        })
        .state('app', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: '应用系统'
            }
        })
        .state('app.app-list', {
            url: "/app-list?page&sort",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-list.html',
                    controller: 'AppListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '应用列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('app.app-monitor', {
            url: "/app-monitor?page&sort",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-monitor.html',
                    controller: 'AppMonitorController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '应用监控'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('app.app-config', {
            url: "/app-config?page&sort",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-config.html',
                    controller: 'AppConfigController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '应用配置'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('app.app-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建应用信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg'
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('app.app-list.edit', {
            url: '/edit/{name}',
            data: {
                pageTitle: '编辑应用信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-dialog.html',
                    controller: 'AppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AppService', function (AppService) {
                            return AppService.get({name: $stateParams.name}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('app.app-list.view', {
            url: '/view/{name}',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/app-details.html',
                    controller: 'AppDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看应用信息'
            },
            resolve: {
                entity: ['AppService', '$stateParams', function (AppService, $stateParams) {
                    return AppService.get({name: $stateParams.name}).$promise;
                }]
            }
        })
        .state('app.app-config.view', {
            url: '/view/{appName}',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app/appConfig-details.html',
                    controller: 'AppConfigViewController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看受监控应用信息'
            },
            resolve: {
                entity: ['AppConfigService', '$stateParams', function (AppConfigService, $stateParams) {
                    return AppConfigService.findByName({appName: $stateParams.appName}).$promise;
                }]
            }
        })
        .state('app.app-authority-list', {
            url: "/app-authority-list?page&sort&appName&authorityName",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/app-authority/app-authority-list.html',
                    controller: 'AppAuthorityListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '应用权限列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        appName: $stateParams.appName,
                        authorityName: $stateParams.authorityName
                    };
                }]
            }
        })
        .state('app.app-authority-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建应用权限信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app-authority/app-authority-dialog.html',
                    controller: 'AppAuthorityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg'
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('user-authority', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: '用户权限'
            }
        })
        .state('user-authority.authority-list', {
            url: "/authority-list?page&sort",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/authority/authority-list.html',
                    controller: 'AuthorityListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '权限列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'name,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {};
                }]
            }
        })
        .state('user-authority.authority-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建权限信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/authority/authority-dialog.html',
                    controller: 'AuthorityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg'
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('user-authority.user-list', {
            url: '/user-list?page&sort&login',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/user/user-list.html',
                    controller: 'UserListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '用户列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'modifiedTime,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        login: $stateParams.login
                    };
                }]
            }
        })
        .state('user-authority.user-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建用户信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/user/user-dialog.html',
                    controller: 'UserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            userId: null,
                            userName: null,
                            firstName: null,
                            lastName: null,
                            email: null,
                            enabled: true,
                            activated: true,
                            createdBy: null,
                            createdTime: null,
                            modifiedBy: null,
                            modifiedTime: null,
                            resetTime: null,
                            resetKey: null
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('user-authority.user-list.edit', {
            url: '/edit/{userName}',
            data: {
                pageTitle: '编辑用户信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/user/user-dialog.html',
                    controller: 'UserDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserService', function (UserService) {
                            return UserService.get({userName: $stateParams.userName}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('user-authority.user-list.view', {
            url: '/view/{userName}',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/user/user-details.html',
                    controller: 'UserDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看用户信息'
            },
            resolve: {
                entity: ['UserService', '$stateParams', function (UserService, $stateParams) {
                    return UserService.get({userName: $stateParams.userName}).$promise;
                }]
            }
        })
        .state('oauth-client', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: '单点登录'
            }
        })
        .state('oauth-client.oauth-client-list', {
            url: "/oauth-client-list?page&sort&clientDetailId",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth-client/oauth-client-list.html',
                    controller: 'OauthClientListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '单点登录客户端列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'client_id,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        clientDetailId: $stateParams.clientDetailId
                    };
                }]
            }
        })
        .state('oauth-client.oauth-client-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建单点登录客户端信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/oauth-client/oauth-client-dialog.html',
                    controller: 'OauthClientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            clientId: null,
                            resourceIds: null,
                            clientSecret: null,
                            scope: null,
                            authorizedGrantTypes: 'authorization_code,password,refresh_token',
                            webServerRedirectUri: null,
                            authorities: null,
                            accessTokenValidity: null,
                            refreshTokenValidity: null,
                            additionalInformation: '{}',
                            autoapprove: true
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('oauth-client.oauth-client-list.edit', {
            url: '/edit/{id}',
            data: {
                pageTitle: '编辑单点登录客户端信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/oauth-client/oauth-client-dialog.html',
                    controller: 'OauthClientDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OauthClientService', function (OauthClientService) {
                            return OauthClientService.get({clientId: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('oauth-client.oauth-client-list.view', {
            url: '/view/{id}',
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/oauth-client/oauth-client-details.html',
                    controller: 'OauthClientDetailsController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '查看单点登录客户端信息'
            },
            resolve: {
                entity: ['OauthClientService', '$stateParams', function (OauthClientService, $stateParams) {
                    return OauthClientService.get({clientId: $stateParams.id}).$promise;
                }]
            }
        })
        .state('admin-menu-authority', {
            abstract: true,
            parent: 'admin',
            data: {
                pageTitle: '管理菜单权限'
            }
        })
        .state('admin-menu-authority.admin-menu-list', {
            url: "/admin-menu-list?page&sort&app",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/admin-menu/admin-menu-list.html',
                    controller: 'AdminMenuListController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '管理菜单列表'
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'sequence,asc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtils', function ($stateParams, PaginationUtils) {
                    return {
                        page: PaginationUtils.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtils.parsePredicate($stateParams.sort),
                        ascending: PaginationUtils.parseAscending($stateParams.sort)
                    };
                }],
                criteria: ['$stateParams', function ($stateParams) {
                    return {
                        app: $stateParams.app
                    };
                }]
            }
        })
        .state('admin-menu-authority.admin-menu-list.create', {
            url: '/create',
            data: {
                pageTitle: '新建管理菜单信息',
                mode: 'create'
            },
            onEnter: ['$state', '$uibModal', function ($state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/admin-menu/app-config-edit.html',
                    controller: 'AdminMenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            id: null,
                            adminMenuName: null,
                            adminMenuChineseText: null,
                            level: null,
                            link: null,
                            sequence: null,
                            parentMenuId: null,
                            appName: null
                        }
                    }
                }).result.then(function () {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('admin-menu-authority.admin-menu-list.edit', {
            url: '/edit/{id}',
            data: {
                pageTitle: '编辑管理菜单信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/admin-menu/app-config-edit.html',
                    controller: 'AdminMenuDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AdminMenuService', function (AdminMenuService) {
                            return AdminMenuService.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('admin-menu-authority.authority-admin-menu', {
            url: "/authority-admin-menu",
            views: {
                'content@': {
                    templateUrl: 'app/views/admin/authority-admin-menu/authority-admin-menu.html',
                    controller: 'AuthorityAdminMenuController',
                    controllerAs: 'vm'
                }
            },
            data: {
                pageTitle: '权限管理菜单'
            },
            resolve: {}
        })
        .state('app.app-config.edit', {
            url: '/edit/{appName}',
            data: {
                pageTitle: '编辑应用配置信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-config-edit.html',
                    controller: 'AppConfigEditController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AppConfigService', function (AppConfigService) {
                            return AppConfigService.findByName({appName: $stateParams.appName}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })
        .state('app.app-config.create', {
            url: '/create/{appName}',
            data: {
                pageTitle: '创建应用配置信息',
                mode: 'edit'
            },
            onEnter: ['$state', '$stateParams', '$uibModal', function ($state, $stateParams, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/views/admin/app/app-config-create.html',
                    controller: 'AppConfigCreateController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: {
                            /*clientId: null,
                            resourceIds: null,
                            clientSecret: null,
                            scope: null,
                            authorizedGrantTypes: 'authorization_code,password,refresh_token',
                            webServerRedirectUri: null,
                            authorities: null,
                            accessTokenValidity: null,
                            refreshTokenValidity: null,
                            additionalInformation: '{}',
                            autoapprove: true*/
                        }
                    }
                }).result.then(function (result) {
                    $state.go('^', null, {reload: true});
                }, function () {
                    $state.go('^');
                });
            }]
        })

};

function paginationConfig(uibPaginationConfig, PAGINATION_CONSTANTS) {
    uibPaginationConfig.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    uibPaginationConfig.maxSize = 5;
    uibPaginationConfig.boundaryLinks = true;
    uibPaginationConfig.firstText = '«';
    uibPaginationConfig.previousText = '‹';
    uibPaginationConfig.nextText = '›';
    uibPaginationConfig.lastText = '»';
};

function pagerConfig(uibPagerConfig, PAGINATION_CONSTANTS) {
    uibPagerConfig.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    uibPagerConfig.previousText = '«';
    uibPagerConfig.nextText = '»';
};

function httpConfig($urlRouterProvider, $httpProvider, httpRequestInterceptorCacheBusterProvider, $urlMatcherFactoryProvider) {
    //Cache everything except rest api requests
    httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

    $httpProvider.interceptors.push('alertErrorHandlerInterceptor');
    $httpProvider.interceptors.push('authExpiredInterceptor');
    $httpProvider.interceptors.push('authInterceptor');
    $httpProvider.interceptors.push('alertHandlerInterceptor');

    $urlMatcherFactoryProvider.type('boolean', {
        name: 'boolean',
        decode: function (val) {
            return val === true || val === 'true';
        },
        encode: function (val) {
            return val ? 1 : 0;
        },
        equals: function (a, b) {
            return this.is(a) && a === b;
        },
        is: function (val) {
            return [true, false, 0, 1].indexOf(val) >= 0;
        },
        pattern: /bool|true|0|1/
    });
};

function localStorageConfig($localStorageProvider, $sessionStorageProvider) {
    $localStorageProvider.setKeyPrefix('app-');
    $sessionStorageProvider.setKeyPrefix('app-');
};

function compileServiceConfig($compileProvider, DEBUG_INFO_ENABLED) {
    // disable debug data on prod profile to improve performance
    $compileProvider.debugInfoEnabled(DEBUG_INFO_ENABLED);

    /*
    If you wish to debug an application with this information
    then you should open up a debug console in the browser
    then call this method directly in this console:

    angular.reloadWithDebugInfo();
    */
};