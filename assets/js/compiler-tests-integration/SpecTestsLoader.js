var SpecTestsLoader = new (function() {
    this.rawGithubUrl = "https://raw.githubusercontent.com/JetBrains/kotlin";
    this.testDataPath = "compiler/tests-spec/testData";
    this.defaultBranch = "spec-tests-draft";
    this.linkedSpecTestsFolder = "linked";

    this.paragraphSelector = [".paragraph", "DL", "UL", "OL"].join(",");
    this.sectionTagNames = ["H1", "H2", "H3", "H4", "H5"];
    this.exceptedSelectors = [".grammar-rule"].join(",");
    this.testTypes = ["pos", "neg"];

    this.loadingIconPath = "./assets/images/loading.gif";
    this.setBranchIcon = "./assets/images/set-branch.png";

    this.prevRequestedSection = null;
    this.loadingOriginalSection = null;
    this.loadedSectionsNumber = 0;

    this.getBranch = function () {
        return window.localStorage.getItem("spec-tests-branch") || this.defaultBranch;
    };

    this.insertLoadIcon = function($headerElement) {
        $headerElement.append(
            '<a href="#" class="set-branch" title="The compiler repo branch from which the tests will be taken"><img src="' + this.setBranchIcon + '" /></a></span>' +
            '<a href="#" data-id="' + $headerElement.attr("id") + '" data-type="' + $headerElement.prop("tagName").toLowerCase() + '" class="load-tests" title="Show tests coverage">Load tests</a>'
        );
    };

    this.getParagraphsInfo = function(sectionElement) {
        var nextSibling = sectionElement[0].nextElementSibling;
        var sectionName = sectionElement.attr("id");
        var paragraphsMap = [];
        var paragraphCounter = 1;

        if (!sectionName)
            return null;

        while (nextSibling) {
            if (this.sectionTagNames.indexOf(nextSibling.tagName) !== -1)
                break;

            var isParagraph = nextSibling.matches(this.paragraphSelector);
            var childParagraph = nextSibling.querySelector(".paragraph");

            if ((isParagraph || childParagraph) && !$(nextSibling).is(this.exceptedSelectors)) {
                var nextParagraph = childParagraph || nextSibling;
                paragraphsMap.push({
                    paragraphElement: nextParagraph,
                    sentenceCount: $(nextParagraph).find(".sentence").length
                })
                paragraphCounter++;
            }
            nextSibling = nextSibling.nextElementSibling;
        }

        return paragraphsMap;
    }

    this.getNestedSections = function($sectionElement) {
        var placeCurrentSectionLevel = this.sectionTagNames.indexOf($sectionElement.prop("tagName").toUpperCase());
        var otherSectionTagNames = this.sectionTagNames.slice(0, placeCurrentSectionLevel + 1);
        var nestedSectionTagNames = this.sectionTagNames.slice(placeCurrentSectionLevel + 1);
        var nextSibling = $sectionElement[0].nextElementSibling;
        var nestedSectionIds = [];

        while (nextSibling) {
            if (otherSectionTagNames.indexOf(nextSibling.tagName) !== -1)
                break;

            if (nestedSectionTagNames.indexOf(nextSibling.tagName) !== -1) {
                nestedSectionIds.push(nextSibling.getAttribute("id"));
            }

            nextSibling = nextSibling.nextElementSibling;
        }

        return nestedSectionIds;
    };

    this.loadUsingApi = function (loader) {
        this.githubGetContentsUrl = "https://api.github.com/repos/JetBrains/Kotlin/contents"

        this.getTestFilesTreeUrl = function(currentSection, sectionsPath) {
            return new Promise(function(resolve, reject) {
                $.get({
                    url: "{1}/{2}/{3}/{4}?ref={5}".format(
                        this.githubGetContentsUrl,
                        loader.testDataPath,
                        loader.linkedSpecTestsFolder,
                        sectionsPath.join("/"),
                        loader.getBranch()
                    ),
                    success: function (data) {
                        resolve(_.result(_.find(data, function(obj) { return obj.name === currentSection }), 'git_url'));
                    },
                    fail: reject
                })
            }.bind(this));
        };

        this.getTestFilesTree = function(url) {
            return new Promise(function(resolve, reject) {
                $.get({ url: url + "?recursive=1", success: resolve, fail: reject })
            });
        };

        this.getTestFilesPromises = function(tree, currentSection, sectionsPath) {
            var promises = [];
            tree.forEach(function(testFile) {
                if (SpecTestsParser.testPathStartingParagraphRegexp.test(testFile.path)) {
                    promises.push(loader.loadFileFromRawGithub('{1}/{2}/{3}'.format(sectionsPath.join("/"), currentSection, testFile.path)));
                }
            });
            return promises;
        };

        this.loadTestFiles = function (sectionName, sectionsPath, paragraphsInfo) {
            return this.getTestFilesTreeUrl(sectionName, sectionsPath)
                .then(this.getTestFilesTree)
                .then(function(response) { return Promise.all(this.getTestFilesPromises(response.tree, sectionName, sectionsPath)) }.bind(this))
        }
    };

    this.loadDirectly = function (loader) {
        this.promisesQueueLength = 0;
        this.testFileContents = [];

        this.tryRequestRun = function (testNumber, pathPrefix, resolve) {
            this.promisesQueueLength++;
            loader.loadFileFromRawGithub(pathPrefix + "." + testNumber + ".kt").then(function(response) {
                this.promisesQueueLength--;
                this.testFileContents.push(response);
                this.tryRequestRun(testNumber + 1, pathPrefix, resolve);
            }.bind(this)).catch(function () {
                this.promisesQueueLength--;
                if (this.promisesQueueLength == 0) {
                    resolve(this.testFileContents);
                }
            }.bind(this))
        };

        this.runSentenseRequests = function (sentenceCount, pathPrefix, resolve) {
            for (var i = 0; i < sentenceCount; i++) {
                loader.testTypes.forEach(function (testType) {
                    this.tryRequestRun(1, (pathPrefix + (i + 1)).format(testType), resolve);
                }.bind(this))
            }
        };

        this.loadTestFiles = function (sectionName, sectionsPath, paragraphsInfo) {
            var self = this;
            var pathPrefix1 = sectionsPath.join("/") + "/" + sectionName + "/p-";
            return new Promise(function (resolve, reject) {
                paragraphsInfo.forEach(function (paragraphInfo, paragraphsIndex) {
                    self.runSentenseRequests(paragraphInfo.sentenceCount, pathPrefix1 + (paragraphsIndex + 1) + "/{1}/", resolve);
                });
            })
        }
    };

    this.loadUsingTestsMapFile = function (loader) {
        this.testsMapFilename = "testsMap.txt"

        this.loadTestsMapFile = function (sectionsPath) {
            return loader.loadFileFromRawGithub(sectionsPath + "/" + this.testsMapFilename);
        };

        this.parseTestsMapFile = function (testsMapText) {
            var testsMapWithoutComments = testsMapText.content.replace(
                new RegExp(SpecTestsParser.MULTILINE_COMMENT_REGEX.format("[\\s\\S]*?")),
                ""
            );
            var paragraphsRegexp = new RegExp(SpecTestsParser.testsByParagraph);
            var paragraphsMatches = paragraphsRegexp.exec(testsMapWithoutComments);
            var startPosition = 0;
            var testsMap = {};

            while (paragraphsMatches != null) {
                startPosition += paragraphsMatches.index + paragraphsMatches[0].length;
                testsMap[paragraphsMatches.groups.paragraphNumber] = {};
                if (paragraphsMatches.groups.negTests) {
                    testsMap[paragraphsMatches.groups.paragraphNumber]["neg"] = paragraphsMatches.groups.negTests.split(" ,")
                }
                if (paragraphsMatches.groups.posTests) {
                    testsMap[paragraphsMatches.groups.paragraphNumber]["pos"] = paragraphsMatches.groups.posTests.split(" ,")
                }
                paragraphsMatches = paragraphsRegexp.exec(testsMapWithoutComments.substr(startPosition));
            }

            return testsMap;
        };

        this.getPromisesForTestFiles = function (testsMap, sectionsPath, sectionName) {
            var promises = [];

            for (paragraph in testsMap) {
                for (testType in testsMap[paragraph]) {
                    testsMap[paragraph][testType].forEach(function (testsBySentences) {
                        testsBySentences.split(", ").forEach(function (testItem) {
                            var testsInfo = testItem.split("-");
                            var sentenceNumber = testsInfo[0];
                            var testsCount = testsInfo[1];
                            for (var i = 1; i <= testsCount; i++) {
                                var testPath = "{1}/{2}/p-{3}/{4}/{5}.{6}.kt".format(
                                    sectionsPath.join("/"),
                                    sectionName,
                                    paragraph,
                                    testType,
                                    sentenceNumber,
                                    i
                                );
                                promises.push(loader.loadFileFromRawGithub(testPath));
                            }
                        })
                    })
                }
            }

            return promises;
        };

        this.loadTestFiles = function (sectionName, sectionsPath, paragraphsInfo) {
            return this.loadTestsMapFile(sectionsPath.join("/") + "/" + sectionName)
                .then(function (testsMapText) {
                    var testsMap = this.parseTestsMapFile(testsMapText);
                    var testFilesPromises = this.getPromisesForTestFiles(testsMap, sectionsPath, sectionName);

                    return Promise.all(testFilesPromises);
                }.bind(this))
                .catch(function () {
                    return Promise.reject();
                })
        }
    };

    this.loadFileFromRawGithub = function(path, folder) {
        return new Promise(function(resolve, reject) {
            $.ajax({
                url: '{1}/{2}/{3}/diagnostics/{4}/{5}'.format(
                    this.rawGithubUrl,
                    this.getBranch(),
                    this.testDataPath,
                    folder || this.linkedSpecTestsFolder,
                    path
                ),
                cache: false,
                type: 'GET',
                error: reject,
                success: function(response) { resolve({ content: response, path: path }) },
            });
        }.bind(this))
    };

    this.parseTestFiles = function(responces, currentSection, sectionsPath, paragraphsInfo) {
        var pathPrefix = sectionsPath.join(".") + "." + currentSection;
        var tests = {};

        responces.forEach(function(response) {
            var objectPath = response.path.replace(".kt", "").replace(/\//g, ".");
            var parsedTest = SpecTestsParser.parseTest(response.content);
            _.set(tests, objectPath, parsedTest);
        });

        SpecTestsCoverageColorsArranger.showCoverage(paragraphsInfo, _.get(tests, pathPrefix));
    };

    this.loadHelperFile = function (helperName) {
        return this.loadFileFromRawGithub(helperName + ".kt", "helpers")
    };

    this.getParantSectionName = function($element, type) {
        return $element.prevAll(type).first().attr("id");
    };

    this.onLoadIconClick = function ($loadIcon) {
        var $section = $loadIcon.parent("h3, h4, h5");
        var paragraphsInfo = this.getParagraphsInfo($section);
        var nestedSections = this.getNestedSections($section);
        var sectionName = $section.attr("id");
        var sectionsPath = [this.getParantSectionName($section, "h2")];

        if (this.loadingOriginalSection == null) {
            this.loadingOriginalSection = sectionName;
            this.loadedSectionsNumber = 1;
        }

        $loadIcon.html("<img src='" + this.loadingIconPath + "' />");

        if ($loadIcon.data("type") == "h4" || $loadIcon.data("type") == "h5") {
            sectionsPath.push(this.getParantSectionName($section, "h3"));
        }
        if ($loadIcon.data("type") == "h5") {
            sectionsPath.push(this.getParantSectionName($section, "h4"));
        }

        this.getRequestGithubDataMethod().loadTestFiles(sectionName, sectionsPath, paragraphsInfo)
            .then(function(testFileContents) {
                this.parseTestFiles(testFileContents, sectionName, sectionsPath, paragraphsInfo);

                $loadIcon.html("Reload tests");
                if (this.loadingOriginalSection == sectionName) {
                    $section.nextAll(".paragraph.with-tests").first()[0].scrollIntoView();
                    this.loadingOriginalSection = null;
                    this.prevRequestedSection = sectionName;
                }
            }.bind(this))
            .catch(function () {
                this.loadedSectionsNumber--;
                if (this.loadingOriginalSection == sectionName) {
                    this.loadingOriginalSection = null;
                    this.prevRequestedSection = sectionName;
                }
                if (this.loadedSectionsNumber == 0) {
                    new Alert({
                        width: 300,
                        title: "Tests not found",
                        content: "Tests for <b>" + this.prevRequestedSection + "</b> in <b>" + this.getBranch() + "</b> branch not yet written. <a href='https://github.com/JetBrains/kotlin/tree/master/compiler/tests-spec/testData' target='_blank'>Contribute it</a>!"
                    });
                }
                $loadIcon.html("Load tests");
            }.bind(this));

        nestedSections.forEach(function (sectionId) {
            this.loadedSectionsNumber++;
            $("#" + sectionId + " .load-tests").click();
        }.bind(this))
    };

    this.onSetBranchIconClick = function () {
        var currentBranch = window.localStorage.getItem("spec-tests-branch") || this.defaultBranch;
        var newBranch = prompt("Specify the Kotlin compiler repo branch from which the spec tests will be taken:", currentBranch);
        if (newBranch && newBranch != currentBranch) {
            window.localStorage.setItem("spec-tests-branch", newBranch);
        }
    };

    this.getRequestGithubDataMethod = function () {
        return new this.loadUsingTestsMapFile(this);
    };
})();
