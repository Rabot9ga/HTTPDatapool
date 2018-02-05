var app = angular.module('HTTPDataPoolFront', []);

app.controller('getCacheContent', function ($scope, $filter, $http, $interval) {
    // init
    $scope.sort = {
        sortingOrder: 'id',
        reverse: false
    };
    $scope.gap = 5;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 150;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    $scope.items = [];

    $scope.tablesAvailableToAdd = [];

    //data typed in input for adding table
    $scope.tableToAdd = '';

    $scope.getTables = function () {


        $http.get("/api/frontEnd/getTables")
            .then(function (response) {
                $scope.items = response.data;
                $scope.search();
            });
    };

    $scope.getTables();

    /**
     * Function used to update adding status of specific table
     * Returns true if adding Table to cache is done
     *
     * @param tableName - name of table which should be
     */
    // function updateAddingStatus(tableName) {
    //     return $http.post("/api/frontEnd/getStatus", tableName)
    //         .then(function (response) {
    //             if (response.data.status === "READY")
    //                 return true;
    //             else
    //                 return false;
    //         });
    // }

    /**
     * Adding table to cache
     * @param name of table to clear
     */
    $scope.addTableCache = function () {
        var name = angular.element(document.querySelector('#tableToAddInput')).val();

        if (name === '') return;
        $scope.tableToAdd = "";

        $http.post("/api/frontEnd/addTable", name)
            .then(function (response) {
                    $scope.items = response.data;
                    $scope.getTables();

                    $scope.updateTablesInDBSuggestionsList();

                    // var stop = $interval(function () {
                    //     var isUpdateDone = updateAddingStatus(name);
                    //     if (isUpdateDone) $interval.cancel(stop);
                    // }, 1000);
                    $.confirm({
                        icon: 'fa fa-spinner fa-spin',
                        title: 'Мы работаем над этим..',
                        content: "Запрос на добавление таблицы в кэш отправлен, но её появление в интерфейсе может произойти не сразу",
                        buttons: {
                            gotIt: {
                                text: 'Я понял',
                                keys: ['enter'],
                                action: function () {
                                    $scope.getTables();
                                }
                            }
                        }
                    });
                },
                function () {
                    $.confirm({
                        title: "Ошибка!",
                        content: "Таблица уже существует!",
                        type: 'red',
                        typeAnimated: true,
                        buttons: {
                            gotIt: {
                                text: 'OK',
                                keys: ['enter']
                            }
                        }
                    });
                });
    };

    /**
     * Confirming intention to clear specific table in cache
     * @param name of table to clear
     */
    $scope.confirmClearTableCache = function (name) {
        $.confirm({
            title: "Вы уверены?",
            content: "Содержимое таблицы кэша пропадет в недрах истории!",
            type: 'red',
            typeAnimated: true,
            escapeKey: true,
            buttons: {
                tryDelete: {
                    text: 'Удаляем!',
                    btnClass: 'btn-red btn-lightgrey',
                    action: function () {
                        $scope.clickClearTableCache(name);
                    }
                },
                close: {
                    text: 'Не уверен',
                    btnClass: 'btn-default btn-cyan',
                    action: function () {
                        $.alert('Не шути с удалением!')
                    }
                }
            }
        });
    };

    /**
     * Confirming intention to update specific table in cache
     * @param name of table to update
     */
    $scope.confirmUpdateTableCache = function (name) {
        $.confirm({
            title: "Вы уверены?",
            content: "Таблица будет загружаться в кэш заново!",
            type: 'red',
            typeAnimated: true,
            escapeKey: true,
            buttons: {
                tryDelete: {
                    text: 'Обновляем!',
                    btnClass: 'btn-red btn-transparent',
                    action: function () {
                        $scope.clickUpdateTableCache(name);
                    }
                },
                close: {
                    text: 'Не уверен',
                    btnClass: 'btn-default btn-cyan',
                    action: function () {
                    }
                }
            }
        });
    };

    /**
     * Clearing table cache
     * @param name of table to clear
     */
    $scope.clickClearTableCache = function (name) {
        console.log("Clearing table: " + name);

        $http.post("/api/frontEnd/clearTable", name)
            .then(function (response) {
                $scope.items = response.data;
                $scope.getTables();
            });
    };

    /**
     * Clearing table cache with Update
     * @param name of table to clear
     */
    $scope.clickUpdateTableCache = function (name) {
        console.log("Updating table: " + name);

        $http.post("/api/frontEnd/clearTable", name)
            .then(function (response) {
                $scope.items = response.data;
                $http.post("/api/frontEnd/addTable", name)
                    .then(function (response) {
                        $scope.items = response.data;
                        $scope.getTables();
                    });
            });
    };

    /**
     * Function used to disable clearing table while it is being added
     *
     * @param status of adding table
     */
    // $scope.isTableAddingStatus = function (status) {
    //     if (status === "UPDATING") {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // };

    /**
     * Auto refreshing table with caches
     */
    $scope.enableAutoRefreshTable = function () {
        var stop = $interval(function () {
            if (!$scope.enableAutoRefreshTableCheckbox) {
                $interval.cancel(stop);
            } else {
                $scope.getTables();
            }
        }, 1000);
    };

    $scope.checkIfEnterWasPressed = function ($event) {
        if ($event.keyCode === 13) {
            // $scope.addTableCache($scope.tableToAdd);
            $scope.addTableCache();
        } else {
            return $event.stopImmediatePropagation();
        }
    };


    $scope.processListOfAvailableTables = function (listOfTablesInDB) {
        //FIX ME - this functionality is in progress

        // var availableTables = [];
        //
        // angular.forEach(listOfTablesInDB, function (tableInDB) {
        //     var isAdded = false;
        //     angular.forEach($scope.items, function (itemAdded) {
        //         if (itemAdded.name === tableInDB) {
        //             // availableTables.push(tableInDB);
        //             isAdded = true;
        //         }
        //     });
        //     if (!isAdded) availableTables.push(tableInDB);
        // }, availableTables);
        // return availableTables;
        return listOfTablesInDB;
    };


    $scope.updateTablesInDBSuggestionsList = function () {
        $http.get("/api/frontEnd/getTableListInDB")
            .then(function (response) {
                // $scope.tablesInDB = response.data;
                $scope.tablesAvailableToAdd = $scope.processListOfAvailableTables(response.data);

                /**
                 * init tableToAdd model
                 */
                $("#tableToAddInput").autocomplete({
                    source: [$scope.tablesAvailableToAdd]
                });
            });
    };

    // this call is needed to prevent from clicking input twice cuz of updating lag
    $scope.updateTablesInDBSuggestionsList();

    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
        // return haystack.indexOf(needle) !== -1;
    };

    // init the filtered items
    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.items, function (item) {
            // for (var attr in item) {
            //     if (searchMatch(item[attr], $scope.query))
            //         return true;
            // }
            // for (var attr in item) {
                if (searchMatch(item["name"], $scope.query))
                    return true;
            // }
            return false;
        });
        // take care of the sorting order
        if ($scope.sort.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sort.sortingOrder, $scope.sort.reverse);
        }
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    };

    // calculate page in place
    $scope.groupToPages = function () {
        $scope.pagedItems = [];

        for (var i = 0; i < $scope.filteredItems.length; i++) {
            if (i % $scope.itemsPerPage === 0) {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [$scope.filteredItems[i]];
            } else {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
            }
        }
    };

    $scope.range = function (size, start, end) {
        var ret = [];
        // console.log(size, start, end);

        if (size < end) {
            end = size;
            start = ((size - $scope.gap) < 0) ? 0 : (size - $scope.gap);
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
    };

    $scope.setPage = function (goToPage) {
        if (goToPage != undefined) {
            this.n = goToPage;
        }
        $scope.currentPage = this.n;
    };
});


app.$inject = ['$scope', '$filter'];

app.directive("customSort", function () {
    return {
        restrict: 'A',
        transclude: true,
        scope: {
            order: '=',
            sort: '='
        },
        template:
        ' <a ng-click="sort_by(order)" style="color: #555555;">' +
        '    <span ng-transclude></span>' +
        '    <i ng-class="selectedCls(order)"></i>' +
        '</a>',
        link: function (scope) {

            // change sorting order
            scope.sort_by = function (newSortingOrder) {
                var sort = scope.sort;

                if (sort.sortingOrder == newSortingOrder) {
                    sort.reverse = !sort.reverse;
                }

                sort.sortingOrder = newSortingOrder;
            };


            scope.selectedCls = function (column) {
                if (column == scope.sort.sortingOrder) {
                    return ('fa fa-chevron-' + ((scope.sort.reverse) ? 'down' : 'up'));
                }
                else {
                    return 'fa fa-sort'
                }
            };
        }// end link
    }
});

/**
 * Metrics controller
 */

app.controller('getMonitoringContent', function ($scope, $filter, $http, $interval) {
    $scope.metrics = [];

    $scope.getMetrics = function () {
        $http.get("/api/frontEnd/getMetrics")
            .then(function (response) {
                $scope.metrics = response.data;
                // $scope.search();
            });
    };

    /**
     * Auto refreshing table with metrics
     */
    $scope.enableAutoRefreshTable = function () {
        var stop = $interval(function () {
            if (!$scope.enableAutoRefreshTableCheckbox) {
                $interval.cancel(stop);
            } else {
                $scope.getMetrics();
            }
        }, 1000);
    };

    $scope.getMetrics();
});
