/**
 * Controllers
 */
angular
    .module('smartcloudserviceApp')
    .controller('MainController', MainController)
    .controller('ApiResponseTimeController', ApiResponseTimeController)
    .controller('ApiAccessController', ApiAccessController)
    .controller('LeftSidebarController', LeftSidebarController)
    .controller('ErrorPageController', ErrorPageController)
    .controller('LoginController', LoginController)
    .controller('NavbarController', NavbarController)
    .controller('ProfileController', ProfileController)
    .controller('RegisterController', RegisterController)
    .controller('ActivationController', ActivationController)
    .controller('ForgotPasswordController', ForgotPasswordController)
    .controller('ResetPasswordController', ResetPasswordController)
    .controller('PasswordController', PasswordController)
    .controller('AppListController', AppListController)
    .controller('AppConfigController', AppConfigController)
    .controller('AppMonitorController', AppMonitorController)
    .controller('AppDialogController', AppDialogController)
    .controller('AppDetailsController', AppDetailsController)
    .controller('AuthorityListController', AuthorityListController)
    .controller('AuthorityDialogController', AuthorityDialogController)
    .controller('AppAuthorityListController', AppAuthorityListController)
    .controller('AppAuthorityDialogController', AppAuthorityDialogController)
    .controller('OauthClientListController', OauthClientListController)
    .controller('OauthClientDialogController', OauthClientDialogController)
    .controller('OauthClientDetailsController', OauthClientDetailsController)
    .controller('AdminMenuListController', AdminMenuListController)
    .controller('AdminMenuDialogController', AdminMenuDialogController)
    .controller('AuthorityAdminMenuController', AuthorityAdminMenuController)
    .controller('UserListController', UserListController)
    .controller('UserDialogController', UserDialogController)
    .controller('UserDetailsController', UserDetailsController)
    .controller('MetricsController', MetricsController)
    .controller('MetricsModalController', MetricsModalController)
    .controller('HealthController', HealthController)
    .controller('HealthModalController', HealthModalController)
    .controller('ConfigurationController', ConfigurationController)
    .controller('BeansController', BeansController)
    .controller('MappingsController', MappingsController)
    .controller('TraceController', TraceController)
    .controller('AuditsController', AuditsController)
    .controller('DictListController', DictListController)
    .controller('DictDialogController', DictDialogController)
    .controller('DictItemListController', DictItemListController)
    .controller('DictItemDialogController', DictItemDialogController)
    .controller('LogsController', LogsController)
    .controller('RedisAdminController', RedisAdminController)
    .controller('ControlController', ControlController);

/**
 * MainController - controller
 * Contains several global data used in different view
 *
 */
function MainController($http, $scope, $state, AuthenticationService, PrincipalService, AuthorityAdminMenuService, AlertUtils, APP_NAME) {
    var main = this;
    main.account = null;
    main.isAuthenticated = null;
    main.links = [];
    main.selectedLink = null;
    main.selectLink = selectLink;

    // Authenticate user whether has logged in
    AuthenticationService.authorize(false, getAccount);

    $scope.$on('authenticationSuccess', function () {
        getAccount();
    });

    $scope.$watch(PrincipalService.isAuthenticated, function () {
        loadLinks();
    });

    function loadLinks() {
        if (PrincipalService.isAuthenticated() == true) {
            main.links = AuthorityAdminMenuService.queryLinks({appName: APP_NAME});
        }
    }

    function getAccount() {
        PrincipalService.identity().then(function (account) {
            main.account = account;
            main.isAuthenticated = PrincipalService.isAuthenticated;

            if (account) {
                AlertUtils.success('登录成功');
            }
        });
    }

    function selectLink($item, $model, $label, $event) {
        $state.go(main.selectedLink.link);
    }
};

/**
 * ApiResponseTimeController
 */
function ApiResponseTimeController() {
    var vm = this;

    /**
     * Options for Line chart
     */
    vm.lineOptions = {
        scaleShowGridLines: true,
        scaleGridLineColor: "rgba(0,0,0,.05)",
        scaleGridLineWidth: 1,
        bezierCurve: true,
        bezierCurveTension: 0.4,
        pointDot: true,
        pointDotRadius: 4,
        pointDotStrokeWidth: 1,
        pointHitDetectionRadius: 20,
        datasetStroke: true,
        datasetStrokeWidth: 2,
        datasetFill: true
    };

    /**
     * Data for Line chart
     */
    vm.lineData = {
        labels: ["January", "February", "March", "April", "May", "June", "July"],
        datasets: [
            {
                label: "Example dataset",
                fillColor: "rgba(26,179,148,0.5)",
                strokeColor: "rgba(26,179,148,0.7)",
                pointColor: "rgba(26,179,148,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(26,179,148,1)",
                data: [28, 48, 40, 19, 86, 27, 90]
            }
        ]
    };
};

function ApiAccessController() {
    var vm = this;

    /**
     * Options for Line chart
     */
    vm.lineOptions = {
        scaleShowGridLines: true,
        scaleGridLineColor: "rgba(0,0,0,.05)",
        scaleGridLineWidth: 1,
        bezierCurve: true,
        bezierCurveTension: 0.4,
        pointDot: true,
        pointDotRadius: 4,
        pointDotStrokeWidth: 1,
        pointHitDetectionRadius: 20,
        datasetStroke: true,
        datasetStrokeWidth: 2,
        datasetFill: true
    };

    /**
     * Data for Line chart
     */
    this.lineData = {
        labels: ["January", "February", "March", "April", "May", "June", "July"],
        datasets: [
            {
                label: "Example dataset",
                fillColor: "rgba(26,179,148,0.5)",
                strokeColor: "rgba(26,179,148,0.7)",
                pointColor: "rgba(26,179,148,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(26,179,148,1)",
                data: [3265, 3159, 2830, 2281, 2756, 2355, 1940]
            }
        ]
    };
};

/**
 * LeftSidebarController
 */
function LeftSidebarController($scope, $state, $element, $timeout, APP_NAME, AuthorityAdminMenuService, PrincipalService) {
    var vm = this;

    vm.init = init;
    vm.groups = [];

    $scope.$watch(PrincipalService.isAuthenticated, function () {
        vm.init();
    });

    function init() {
        if (PrincipalService.isAuthenticated() == true) {
            AuthorityAdminMenuService.query({appName: APP_NAME}, function (response) {
                if (response.length > 0) {
                    vm.groups = response;
                    // Call the metsiMenu plugin and plug it to sidebar navigation
                    $timeout(function () {
                        $element.metisMenu();
                    });
                }
            }, function (errorResponse) {
            });
        }
    }
};


/**
 * ErrorPageController
 */
function ErrorPageController($state, $stateParams, $scope, JSONFormatterConfig) {
    var vm = this;

    vm.errorMessage = $stateParams.errorMessage;
};

/**
 * LoginController
 */
function LoginController($rootScope, $state, AuthenticationService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.userName = null;
    vm.password = null;
    vm.errorMsg = null;
    vm.login = login;
    vm.requestResetPassword = requestResetPassword;
    vm.isSaving = false;

    function login(event) {
        event.preventDefault();
        vm.isSaving = true;
        AuthenticationService.login({
                userName: vm.userName,
                password: vm.password,
                rememberMe: vm.rememberMe
            },
            function (data) {
                vm.errorMsg = null;
//            if ($state.current.name === 'register' || $state.current.name === 'activate' ||
//                $state.current.name === 'finishReset' || $state.current.name === 'requestReset') {
//                $state.go('home');
//            }

                $rootScope.$broadcast('authenticationSuccess');

                // previousState was set in the authExpiredInterceptor before being redirected to login modal.
                // since login is successful, go to stored previousState and clear previousState
                if (AuthenticationService.getPreviousState()) {
                    var previousState = AuthenticationService.getPreviousState();
                    AuthenticationService.resetPreviousState();
                    $state.go(previousState.name, previousState.params);
                }

                $state.go('dashboard');
            },
            function (data) {
                vm.errorMsg = data.error_description;
                vm.isSaving = false;
            });
    }

    function requestResetPassword() {
        $state.go('requestReset');
    }
};

/**
 * NavbarController
 */
function NavbarController($rootScope, $scope, $translate, $state, AuthenticationService, PrincipalService, ProfileService) {
    var vm = this;

    vm.isNavbarCollapsed = true;
    vm.isAuthenticated = PrincipalService.isAuthenticated;
    vm.changeLanguage = changeLanguage;

    ProfileService.getProfileInfo().then(function (response) {
        vm.inProduction = response.inProduction;
        vm.swaggerDisabled = response.swaggerDisabled;
    });

    vm.logout = logout;
    vm.toggleNavbar = toggleNavbar;
    vm.collapseNavbar = collapseNavbar;
    vm.$state = $state;

    $rootScope.isNavbarLoaded = true;

    function changeLanguage(langKey) {
        $translate.use(langKey);
        $scope.language = langKey;
    };

    function logout() {
        AuthenticationService.logout();
        $state.go('login');
    }

    function toggleNavbar() {
        vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
    }

    function collapseNavbar() {
        vm.isNavbarCollapsed = true;
    }
};

/**
 * ProfileController
 */
function ProfileController($state, PrincipalService, AccountService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.save = save;
    vm.isSaving = false;
    vm.profileAccount = null;

    /**
     * Store the "profile account" in a separate variable, and not in the shared "account" variable.
     */
    var copyAccount = function (account) {
        return {
            activated: account.activated,
            email: account.email,
            mobileNo: parseInt(account.mobileNo),
            firstName: account.firstName,
            lastName: account.lastName,
            userName: account.userName,
            avatarImageUrl: account.avatarImageUrl
        };
    };

    PrincipalService.identity().then(function (account) {
        vm.profileAccount = copyAccount(account);
    });

    function save() {
        vm.isSaving = true;
        AccountService.update(vm.profileAccount,
            function (response) {
                vm.isSaving = false;
                PrincipalService.identity(true).then(function (account) {
                    vm.profileAccount = copyAccount(account);
                });
            },
            function (response) {
                vm.isSaving = false;
            });
    }
};

/**
 * RegisterController
 */
function RegisterController($state, $timeout, AuthenticationService, RegisterService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.register = register;
    vm.isSaving = false;
    vm.registerAccount = {};
    vm.passwordNotMatch = false;

    $timeout(function () {
        angular.element('#userName').focus();
    });

    function register() {
        if (vm.registerAccount.password !== vm.confirmPassword) {
            vm.passwordNotMatch = true;
        } else {
            vm.isSaving = true;
            RegisterService.save(vm.registerAccount,
                function (account) {
                    vm.isSaving = false;
                    vm.passwordNotMatch = false;
                    $state.go('login');
                },
                function (response) {
                    AuthenticationService.logout();
                    vm.isSaving = false;
                    vm.passwordNotMatch = false;
                });
        }
    }
};

/**
 * ActivationController
 */
function ActivationController($state, $stateParams, ActivateService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.success = false;
    vm.errorMessage = null;

    if ($stateParams.key) {
        ActivateService.get({key: $stateParams.key},
            function (response) {
                vm.success = true;
                vm.fieldErrors = [];
            },
            function (response, headers) {
                vm.success = false;
                vm.errorMessage = response.data.message;
            });
    }
};

/**
 * ForgotPasswordController
 */
function ForgotPasswordController($state, $timeout, PasswordResetInitService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.requestReset = requestReset;
    vm.resetAccount = {};
    vm.isSaving = false;
    vm.success = false;
    vm.errorMsg = null;

    $timeout(function () {
        angular.element('#email').focus();
    });

    function requestReset() {
        vm.isSaving = true;
        vm.success = false;
        PasswordResetInitService.save(vm.resetAccount.email,
            function (response) {
                vm.isSaving = false;
                vm.success = true;
            },
            function (response) {
                vm.isSaving = false;
                vm.success = false;
                vm.errorMsg = response.error_description;
            });
    }
};

/**
 * ResetPasswordController
 */
function ResetPasswordController($stateParams, $timeout, PasswordResetFinishService, LoginService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.confirmPassword = null;
    vm.isSaving = false;
    vm.finishReset = finishReset;
    vm.login = LoginService.open;
    vm.resetAccount = {};
    vm.success = false;
    vm.error = false;
    vm.passwordNotMatch = false;
    vm.keyMissing = angular.isUndefined($stateParams.key);

    $timeout(function () {
        angular.element('#password').focus();
    });

    function finishReset() {
        vm.passwordNotMatch = false;
        vm.error = false;
        if (vm.resetAccount.password !== vm.confirmPassword) {
            vm.passwordNotMatch = true;
        } else {
            vm.isSaving = true;
            PasswordResetFinishService.save({key: $stateParams.key, newPassword: vm.resetAccount.password},
                function (response) {
                    vm.isSaving = false;
                    vm.success = true;
                },
                function (response) {
                    vm.isSaving = false;
                    vm.success = false;
                    vm.error = true;
                });
        }
    }
};

/**
 * PasswordController
 */
function PasswordController($state, PasswordService, PrincipalService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.save = save;
    vm.isSaving = false;
    vm.passwordNotMatch = false;

    PrincipalService.identity().then(function (account) {
        vm.account = account;
    });

    function save() {
        if (vm.password !== vm.confirmPassword) {
            vm.passwordNotMatch = true;
        } else {
            vm.passwordNotMatch = false;
            vm.isSaving = true;
            PasswordService.update(vm.password,
                function (response) {
                    vm.isSaving = false;
                },
                function (response) {
                    vm.isSaving = false;
                });
        }
    }
};

/**
 * AppListController
 */
function AppListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, AppService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        AppService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort()
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'name') {
            // default sort column
            result.push('name,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        AppService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(name) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                AppService.del({name: name},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

function AppConfigController($state, ParseLinksUtils, pagingParams, AppConfigService) {
    var vm = this;
    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.isSaving = false;
    vm.entity = AppConfigService.findAll();
    function findAll() {
        AppConfigService.findAll({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort()
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'name') {
            // default sort column
            result.push('name,asc');
        }
        return result;
    }
}

function AppMonitorController($state, AppMonitorService) {
    var vm = this;
    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.entity = AppMonitorService.findAll();
    vm.isSaving = false;
    vm.findAll = function findAll() {
        vm.entity = AppMonitorService.findAll();
    }
}

/**
 * AppDialogController
 */
function AppDialogController($state, $stateParams, $uibModalInstance, AppService, AccountService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.authorities = AccountService.queryAuthorityNames({enabled: true});
    vm.entity = {};
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    if (vm.mode === 'create') {
        vm.entity = {
            name: null,
            enabled: true
        };
    }
    else {
        vm.entity = AppService.get({name: $stateParams.name});
    }

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            AppService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            AppService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * AppDetailsController
 */
function AppDetailsController($state, $stateParams, AppService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.grandfatherPageTitle = $state.$current.parent.parent.data.pageTitle;
    vm.entity = entity;
};

/**
 * AuthorityListController
 */
function AuthorityListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, AuthorityService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        AuthorityService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort()
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'name') {
            // default sort column
            result.push('name,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        AuthorityService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(name) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                AuthorityService.del({name: name},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * AuthorityDialogController
 */
function AuthorityDialogController($state, $stateParams, $uibModalInstance, AuthorityService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.entity = {};
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    if (vm.mode === 'create') {
        vm.entity = {
            name: null,
            systemLevel: false,
            enabled: true
        };
    }
    else {
        vm.entity = AuthorityService.get({name: $stateParams.name});
    }

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            AuthorityService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            AuthorityService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * AppAuthorityListController
 */
function AppAuthorityListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, AppAuthorityService, AppService, AuthorityService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.apps = AppService.queryAll();
    vm.authorities = AuthorityService.queryAll();
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        AppAuthorityService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            appName: vm.criteria.appName,
            authorityName: vm.criteria.authorityName
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'appName') {
            // default sort column
            result.push('appName,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            appName: vm.criteria.appName,
            authorityName: vm.criteria.authorityName
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        AppAuthorityService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(id) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                AppAuthorityService.del({id: id},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * AppAuthorityDialogController
 */
function AppAuthorityDialogController($state, $stateParams, $uibModalInstance, AppAuthorityService, AppService, AuthorityService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.apps = AppService.queryAll();
    vm.authorities = AuthorityService.queryAll();
    vm.entity = {};
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    if (vm.mode === 'create') {
        vm.entity = {
            id: null,
            appName: null,
            authorityName: null

        };
    }
    else {
        vm.entity = AppAuthorityService.get({id: $stateParams.id});
    }

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            AppAuthorityService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            AppAuthorityService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * UserListController
 */
function UserListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, UserService, PrincipalService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.currentAccount = null;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setActive = setActive;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;
    vm.resetPassword = resetPassword;

    vm.loadAll();

    PrincipalService.identity().then(function (account) {
        vm.currentAccount = account;
    });

    function loadAll() {
        UserService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            login: vm.criteria.login
        }, function (result, headers) {
            //hide anonymous user from user management: it's a required user for Spring Security
            for (var i in result) {
                if (result[i]['userName'] === 'anonymoususer') {
                    result.splice(i, co1);
                }
            }
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'modifiedTime') {
            // default sort column
            result.push('modifiedTime,desc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            login: vm.criteria.login
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setActive(user, isActivated) {
        user.activated = isActivated;
        UserService.update(user, function () {
                vm.loadAll();
            },
            function () {
                user.activated = !isActivated;
            });
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        UserService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(userName) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                UserService.del({userName: userName},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }

    function resetPassword(userName) {
        AlertUtils.createResetPasswordConfirmation('密码恢复到初始值?', function (isConfirm) {
            if (isConfirm) {
                UserService.resetPassword({userName: userName},
                    function () {
                    });
            }
        });
    }
};

/**
 * UserDialogController
 */
function UserDialogController($state, $stateParams, $uibModalInstance, UserService, AccountService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.authorities = AccountService.queryAuthorityNames({enabled: true});
    vm.entity = entity;
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            UserService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            UserService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * UserDetailsController
 */
function UserDetailsController($state, $stateParams, UserService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.grandfatherPageTitle = $state.$current.parent.parent.data.pageTitle;
    vm.entity = entity;
};

/**
 * OauthClientListController
 */
function OauthClientListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, OauthClientService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        OauthClientService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            clientDetailId: vm.criteria.clientDetailId
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'client_id') {
            // default sort column
            result.push('client_id,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            clientDetailId: vm.criteria.clientDetailId
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function del(id) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                OauthClientService.del({clientId: id},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * OauthClientDialogController
 */
function OauthClientDialogController($state, $stateParams, $uibModalInstance, OauthClientService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.entity = entity;
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            OauthClientService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            OauthClientService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * OauthClientDetailsController
 */
function OauthClientDetailsController($state, $stateParams, OauthClientService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.grandfatherPageTitle = $state.$current.parent.parent.data.pageTitle;
    vm.entity = entity;
};

/**
 * AdminMenuListController
 */
function AdminMenuListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, AdminMenuService, AppService, APP_NAME) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.apps = AppService.queryAll();
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        AdminMenuService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            appName: vm.criteria.app
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'modifiedTime') {
            // default sort column
            result.push('modifiedTime,desc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            app: vm.criteria.app
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function del(id) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                AdminMenuService.del({id: id},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * AdminMenuDialogController
 */
function AdminMenuDialogController($state, $stateParams, $uibModalInstance, AdminMenuService, AppService, APP_NAME, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
//    vm.apps = DictItemService.queryByDictCode({id: 'app'});
    vm.apps = AppService.queryAll();
    vm.searchParentMenus = searchParentMenus;
    vm.entity = entity;
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    vm.searchParentMenus();

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            AdminMenuService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            AdminMenuService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function searchParentMenus() {
        if (vm.entity && vm.entity.appName) {
            vm.parentMenus = AdminMenuService.queryParentMenu({app: vm.entity.appName});
        }
        else {
            vm.parentMenus = [];
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * AuthorityAdminMenuController
 */
function AuthorityAdminMenuController($state, AuthorityAdminMenuService, AppAuthorityService, AppService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.parentPageTitle = $state.$current.parent.data.pageTitle;
    vm.apps = AppService.queryAll();
    vm.authorities = [];
    vm.allMenus = [];
    vm.isSaving = false;
    vm.authorityName = null;
    vm.searchAuthorities = searchAuthorities;
    vm.searchMenus = searchMenus;
    vm.save = save;

    function searchAuthorities() {
        if (vm.criteria && vm.criteria.appName) {
            vm.authorities = AppAuthorityService.queryByAppName({id: vm.criteria.appName});
        }
        else {
            vm.authorities = [];
        }
    }

    function searchMenus() {
        if (vm.criteria.authorityName) {
            AuthorityAdminMenuService.queryMenusByAuthorityName({
                appName: vm.criteria.appName,
                authorityName: vm.criteria.authorityName
            }, function (response) {
                vm.allMenus = response;
            }, function (errorResponse) {
            });
        }
        else {
            vm.allMenus = [];
        }
    }

    function save() {
        vm.isSaving = true;
        if (vm.criteria.appName && vm.criteria.authorityName) {
            var adminMenuIds = [];
            adminMenuIds = getAllCheckIds(vm.allMenus, adminMenuIds);
            AuthorityAdminMenuService.updateAuthorityMenus({
                    appName: vm.criteria.appName,
                    authorityName: vm.criteria.authorityName,
                    adminMenuIds: adminMenuIds
                },
                function (response) {
                    vm.isSaving = false;
                }, function (errorResponse) {
                    vm.isSaving = false;
                });
        }
    }

    function getAllCheckIds(allMenus, adminMenuIds) {
        for (var i = 0; i < allMenus.length; i++) {
            if (allMenus[i].checked) {
                adminMenuIds.push(allMenus[i].id);
            }
            if (allMenus[i].subItems) {
                getAllCheckIds(allMenus[i].subItems, adminMenuIds);
            }
        }
        return adminMenuIds;
    }
};

/**
 * MetricsController
 *
 */
function MetricsController($state, $scope, $uibModal, MetricsService, metrics) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.cachesStats = {};
    vm.metrics = metrics;
    vm.refresh = refresh;
    vm.refreshThreadDumpData = refreshThreadDumpData;
    vm.servicesStats = {};
    vm.updatingMetrics = false;
    /**
     * Options for Doughnut chart
     */
    vm.doughnutOptions = {
        segmentShowStroke: true,
        segmentStrokeColor: "#fff",
        segmentStrokeWidth: 2,
        percentageInnerCutout: 45, // This is 0 for Pie charts
        animationSteps: 100,
        animationEasing: "easeOutBounce",
        animateRotate: true,
        animateScale: false
    };

    vm.totalMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.total.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.total.max'].value - vm.metrics.gauges['jvm.memory.total.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.heapMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.heap.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.heap.max'].value - vm.metrics.gauges['jvm.memory.heap.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.edenSpaceMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.pools.PS-Eden-Space.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.pools.PS-Eden-Space.max'].value - vm.metrics.gauges['jvm.memory.pools.PS-Eden-Space.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.survivorSpaceMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.pools.PS-Survivor-Space.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.pools.PS-Survivor-Space.max'].value - vm.metrics.gauges['jvm.memory.pools.PS-Survivor-Space.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.oldSpaceMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.pools.PS-Old-Gen.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.pools.PS-Old-Gen.max'].value - vm.metrics.gauges['jvm.memory.pools.PS-Old-Gen.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.nonHeapMemory = [
        {
            value: vm.metrics.gauges['jvm.memory.non-heap.used'].value / 1000000,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "已使用"
        },
        {
            value: (vm.metrics.gauges['jvm.memory.non-heap.committed'].value - vm.metrics.gauges['jvm.memory.non-heap.used'].value) / 1000000,
            color: "#dedede",
            highlight: "#1ab394",
            label: "未使用"
        }
    ];

    vm.runnableThreads = [
        {
            value: vm.metrics.gauges['jvm.threads.runnable.count'].value,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "Runnable"
        },
        {
            value: (vm.metrics.gauges['jvm.threads.count'].value - vm.metrics.gauges['jvm.threads.runnable.count'].value),
            color: "#dedede",
            highlight: "#1ab394",
            label: "Others"
        }
    ];

    vm.timedWaitingThreads = [
        {
            value: vm.metrics.gauges['jvm.threads.timed_waiting.count'].value,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "Timed waiting"
        },
        {
            value: (vm.metrics.gauges['jvm.threads.count'].value - vm.metrics.gauges['jvm.threads.timed_waiting.count'].value),
            color: "#dedede",
            highlight: "#1ab394",
            label: "Others"
        }
    ];

    vm.waitingThreads = [
        {
            value: vm.metrics.gauges['jvm.threads.waiting.count'].value,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "Waiting"
        },
        {
            value: (vm.metrics.gauges['jvm.threads.count'].value - vm.metrics.gauges['jvm.threads.waiting.count'].value),
            color: "#dedede",
            highlight: "#1ab394",
            label: "Others"
        }
    ];

    vm.blockedThreads = [
        {
            value: vm.metrics.gauges['jvm.threads.blocked.count'].value,
            color: "#a3e1d4",
            highlight: "#1ab394",
            label: "Blocked"
        },
        {
            value: (vm.metrics.gauges['jvm.threads.count'].value - vm.metrics.gauges['jvm.threads.blocked.count'].value),
            color: "#dedede",
            highlight: "#1ab394",
            label: "Others"
        }
    ];

    $scope.$watch('vm.metrics', function (newValue) {
        vm.servicesStats = {};
        vm.cachesStats = {};
        angular.forEach(newValue.timers, function (value, key) {
            if (key.indexOf('controller.') !== -1) {
                var controllerStartIndex = key.indexOf('controller.');
                var offset = 'controller.'.length;
                vm.servicesStats[key.substr(controllerStartIndex + offset, key.length)] = value;
            }
            if (key.indexOf('service.impl.') !== -1) {
                var controllerStartIndex = key.indexOf('service.impl.');
                var offset = 'service.impl.'.length;
                vm.servicesStats[key.substr(controllerStartIndex + offset, key.length)] = value;
            }
            if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
                // remove gets or puts
                var index = key.lastIndexOf('.');
                var newKey = key.substr(0, index);

                // Keep the name of the domain
                index = newKey.lastIndexOf('.');
                vm.cachesStats[newKey] = {
                    'name': newKey.substr(index + 1),
                    'value': value
                };
            }
        });
    });

    function refresh() {
        vm.updatingMetrics = true;
        MetricsService.getMetrics().then(function (promise) {
            vm.metrics = promise;
            vm.updatingMetrics = false;
        }, function (promise) {
            vm.metrics = promise.data;
            vm.updatingMetrics = false;
        });
    }

    function refreshThreadDumpData() {
        MetricsService.threadDump().then(function (data) {
            $uibModal.open({
                templateUrl: 'app/views/developer/metrics/metrics.modal.html',
                controller: 'MetricsModalController',
                controllerAs: 'vm',
                size: 'lg',
                resolve: {
                    threadDump: function () {
                        return data;
                    }
                }
            });
        });
    }
};

/**
 * MetricsModalController
 *
 */
function MetricsModalController($uibModalInstance, threadDump) {
    var vm = this;

    vm.cancel = cancel;
    vm.getLabelClass = getLabelClass;
    vm.threadDump = threadDump;
    vm.threadDumpAll = 0;
    vm.threadDumpBlocked = 0;
    vm.threadDumpRunnable = 0;
    vm.threadDumpTimedWaiting = 0;
    vm.threadDumpWaiting = 0;

    angular.forEach(threadDump, function (value) {
        if (value.threadState === 'RUNNABLE') {
            vm.threadDumpRunnable += 1;
        } else if (value.threadState === 'WAITING') {
            vm.threadDumpWaiting += 1;
        } else if (value.threadState === 'TIMED_WAITING') {
            vm.threadDumpTimedWaiting += 1;
        } else if (value.threadState === 'BLOCKED') {
            vm.threadDumpBlocked += 1;
        }
    });

    vm.threadDumpAll = vm.threadDumpRunnable + vm.threadDumpWaiting +
        vm.threadDumpTimedWaiting + vm.threadDumpBlocked;

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }

    function getLabelClass(threadState) {
        if (threadState === 'RUNNABLE') {
            return 'label-success';
        } else if (threadState === 'WAITING') {
            return 'label-info';
        } else if (threadState === 'TIMED_WAITING') {
            return 'label-warning';
        } else if (threadState === 'BLOCKED') {
            return 'label-danger';
        }
    }
};

function HealthController($state, HealthService, $uibModal) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.updatingHealth = true;
    vm.getLabelClass = getLabelClass;
    vm.refresh = refresh;
    vm.showHealth = showHealth;
    vm.baseName = HealthService.getBaseName;
    vm.subSystemName = HealthService.getSubSystemName;

    vm.refresh();

    function getLabelClass(statusState) {
        if (statusState === 'UP') {
            return 'label-primary';
        } else {
            return 'label-danger';
        }
    }

    function refresh() {
        vm.updatingHealth = true;
        HealthService.checkHealth().then(function (response) {
            vm.healthData = HealthService.transformHealthData(response);
            vm.updatingHealth = false;
        }, function (response) {
            vm.healthData = HealthService.transformHealthData(response.data);
            vm.updatingHealth = false;
        });
    }

    function showHealth(health) {
        $uibModal.open({
            templateUrl: 'app/views/developer/health/health.modal.html',
            controller: 'HealthModalController',
            controllerAs: 'vm',
            size: 'lg',
            resolve: {
                currentHealth: function () {
                    return health;
                },
                baseName: function () {
                    return vm.baseName;
                },
                subSystemName: function () {
                    return vm.subSystemName;
                }
            }
        });
    }
};

function HealthModalController($uibModalInstance, currentHealth, baseName, subSystemName) {
    var vm = this;

    vm.cancel = cancel;
    vm.currentHealth = currentHealth;
    vm.baseName = baseName;
    vm.subSystemName = subSystemName;

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

function ConfigurationController($state, ConfigurationService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.allConfiguration = null;
    vm.configuration = null;

    ConfigurationService.get().then(function (configuration) {
        vm.configuration = configuration;
    });
    ConfigurationService.getEnv().then(function (configuration) {
        vm.allConfiguration = configuration;
    });
};

function BeansController($state, $http) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.items = null;
    vm.refresh = refresh;
    vm.refresh();

    function refresh() {
        $http.get('management/beans').then(function (response) {
            vm.items = response.data[0].beans;
        });
    }
};

function MappingsController($state, $http) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.items = new Array();
    vm.refresh = refresh;
    vm.refresh();

    function refresh() {
        $http.get('management/mappings').then(function (response) {
            for (var key in response.data) {
                var value = response.data[key];
                vm.items.push({url: key, object: value});
            }
        });
    }
};

function TraceController($state, $http) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.refresh = refresh;
    vm.refresh();

    function refresh() {
        $http.get('management/trace').then(function (response) {
            vm.items = response.data;
        });
    }
};

function AuditsController($state, $filter, AuditsService, ParseLinksUtils, PAGINATION_CONSTANTS) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.entities = null;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.page = 1;
    vm.itemsPerPage = 20;
    vm.previousMonth = previousMonth;
    // Previous 1 month
    vm.fromDate = null;
    // Tomorrow
    vm.toDate = null;
    vm.today = today;
    vm.totalItems = null;
    vm.predicate = 'auditEventDate';
    vm.reverse = false;

    vm.today();
    vm.previousMonth();
    vm.loadAll();

    function loadAll() {
        var dateFormat = 'yyyy-MM-dd';
        var fromDate = $filter('date')(vm.fromDate, dateFormat);
        var toDate = $filter('date')(vm.toDate, dateFormat);
        AuditsService.query({
            page: vm.page - 1,
            size: vm.itemsPerPage,
            sort: [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')],
            from: fromDate,
            to: toDate
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.entities = result;
        });
    }

    // Date picker configuration
    function today() {
        // Today + 1 day - needed if the current day must be included
        var today = new Date();
        vm.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
    }

    function previousMonth() {
        var fromDate = new Date();
        if (fromDate.getMonth() === 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }
        vm.fromDate = fromDate;
    }
};

/**
 * DictListController
 */
function DictListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, DictService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        DictService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            dictName: vm.criteria.dictName
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'dictCode') {
            // default sort column
            result.push('dictCode,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            dictName: vm.criteria.dictName
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        DictService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(id) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                DictService.del({id: id},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * DictDialogController
 */
function DictDialogController($state, $stateParams, $uibModalInstance, DictService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.mode = $state.current.data.mode;
    vm.entity = entity;
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            DictService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            DictService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * DictItemListController
 */
function DictItemListController($state, AlertUtils, ParseLinksUtils, PAGINATION_CONSTANTS, pagingParams, criteria, DictService, DictItemService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.dicts = DictService.queryAll();
    vm.links = null;
    vm.loadAll = loadAll;
    vm.loadPage = loadPage;
    vm.checkPressEnter = checkPressEnter;
    vm.page = 1;
    vm.setEnabled = setEnabled;
    vm.totalItems = null;
    vm.entities = [];
    vm.predicate = pagingParams.predicate;
    vm.reverse = pagingParams.ascending;
    vm.itemsPerPage = PAGINATION_CONSTANTS.itemsPerPage;
    vm.transition = transition;
    vm.criteria = criteria;
    vm.del = del;

    vm.loadAll();

    function loadAll() {
        DictItemService.query({
            page: pagingParams.page - 1,
            size: vm.itemsPerPage,
            sort: sort(),
            dictCode: vm.criteria.dictCode,
            dictItemName: vm.criteria.dictItemName
        }, function (result, headers) {
            vm.links = ParseLinksUtils.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.page = pagingParams.page;
            vm.entities = result;
        });
    }

    function sort() {
        var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
        if (vm.predicate !== 'dictCode') {
            // default sort column
            result.push('dictCode,asc');
        }
        return result;
    }

    function loadPage(page) {
        vm.page = page;
        vm.transition();
    }

    function transition() {
        $state.transitionTo($state.$current, {
            page: vm.page,
            sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
            dictCode: vm.criteria.dictCode,
            dictItemName: vm.criteria.dictItemName
        });
    }

    function checkPressEnter($event) {
        //按下enter键重新查询数据 
        if ($event.keyCode == 13) {
            vm.transition();
        }
    }

    function setEnabled(entity, enabled) {
        entity.enabled = enabled;
        DictItemService.update(entity,
            function () {
                vm.loadAll();
            },
            function () {
                entity.enabled = !enabled;
            });
    }

    function del(id) {
        AlertUtils.createDeleteConfirmation('数据有可能被其他数据所引用，删除之后可能出现一些问题，您确定删除吗?', function (isConfirm) {
            if (isConfirm) {
                DictItemService.del({id: id},
                    function () {
                        vm.loadAll();
                    },
                    function () {
                    });
            }
        });
    }
};

/**
 * DictItemDialogController
 */
function DictItemDialogController($state, $stateParams, $uibModalInstance, DictService, DictItemService, entity) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.dicts = DictService.queryAll({enabled: true});
    vm.mode = $state.current.data.mode;
    vm.entity = entity;
    vm.isSaving = false;
    vm.save = save;
    vm.cancel = cancel;

    function save() {
        vm.isSaving = true;
        if (vm.mode == 'edit') {
            DictItemService.update(vm.entity, onSaveSuccess, onSaveError);
        } else {
            DictItemService.save(vm.entity, onSaveSuccess, onSaveError);
        }
    }

    function onSaveSuccess(result) {
        vm.isSaving = false;
        $uibModalInstance.close(result);
    }

    function onSaveError(result) {
        vm.isSaving = false;
    }

    function cancel() {
        $uibModalInstance.dismiss('cancel');
    }
};

/**
 * LogsController
 */
function LogsController($state, LogsService) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.changeLevel = changeLevel;
    vm.loggers = LogsService.findAll();

    function changeLevel(name, level) {
        LogsService.changeLevel({name: name, level: level}, function () {
            vm.loggers = LogsService.findAll();
        });
    }
};

function RedisAdminController($localStorage) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.url = 'system/redis/admin?access_token=' + $localStorage.authenticationToken.access_token;
};

function ControlController($state, $http, AlertUtils) {
    var vm = this;

    vm.pageTitle = $state.current.data.pageTitle;
    vm.items = null;
    vm.shutdown = shutdown;

    function shutdown() {
        $http.post('management/shutdown').then(function (response) {
                AlertUtils.success("Shutdown successfully", {});
            },
            function (response) {
                AlertUtils.error("Shutdown failed", {});
            });
    }
};
