var app = angular.module('HTTPDataPoolFront', []);

app.controller('getCacheContent', function ($scope, $filter, $http) {
    // init
    $scope.sort = {
        sortingOrder: 'id',
        reverse: false
    };
    $scope.gap = 5;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    $scope.items = [];

    $scope.getTables = function () {
        console.log("getTables executed");

        $http.get("/api/frontEnd/getTables")
            .then(function (response) {
                console.log("getTables: $scope.items b4: " + $scope.items);
                $scope.items = response.data;
                console.log("getTables: $scope.items after: " + $scope.items);
                // functions have been describe process the data for display
                $scope.search();
            });
    };

    $scope.getTables();

    /**
     * Adding table to cache
     * @param name of table to clear
     */
    $scope.addTableCache = function (name) {


        console.log("Adding tableName: " + name);

        $http.post("/api/frontEnd/addTable", name)
            .then(function (response) {
                // console.log("addTableCache: $scope.items b4: " + $scope.items);
                $scope.items = response.data;
                // console.log("addTableCache: $scope.items after: " + $scope.items);
                // $scope.search();

                // console.log("inside then function. Ready to execute getTables");
                $scope.getTables();
            });

    }
    /**
     * Clearing table cache
     * @param name of table to clear
     */
    $scope.clickClearTableCache = function (name) {
        console.log("Clearing table: " + name);

        $http.post("/api/frontEnd/clearTable", name)
            .then(function (response) {
                // console.log("clearTable: $scope.items b4: " + $scope.items);
                $scope.items = response.data;
                // console.log("clearTable: $scope.items b4: " + $scope.items);
                // $scope.search();


                $scope.getTables();
            });

    };

    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };

    // init the filtered items
    $scope.search = function () {



        $scope.filteredItems = $filter('filter')($scope.items, function (item) {
            for (var attr in item) {
                if (searchMatch(item[attr], $scope.query))
                    return true;
            }
            return false;
        });
        // take care of the sorting order
        if ($scope.sort.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sort.sortingOrder, $scope.sort.reverse);
        }
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();


        console.log("in search");
        console.log("$scope.pagedItems:");
        console.log($scope.pagedItems);
        console.log("$scope.groupedItems:");
        console.log($scope.groupedItems);
        console.log("$scope.filteredItems:");
        console.log($scope.filteredItems);
        console.log("$scope.query:");
        console.log($scope.query);
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
                    return ('icon-chevron-' + ((scope.sort.reverse) ? 'down' : 'up'));
                }
                else {
                    return 'icon-sort'
                }
            };
        }// end link
    }
});
