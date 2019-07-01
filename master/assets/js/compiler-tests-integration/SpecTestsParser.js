var SpecTestsParser = new (function() {
    this.SINGLE_LINE_COMMENT_REGEX = "\\/\\/\\s*{1}";
    this.MULTILINE_COMMENT_REGEX = "\\/\\*\\s+?{1}\\s+\\*\\/(?:\\n)*";
    this.TEST_CASE_CODE_REGEX = "(?<{1}>[\\s\\S]*?)"
    this.ASTERISK_REGEX = "\\*"
    this.directiveRegex = this.SINGLE_LINE_COMMENT_REGEX.format("[\\w\\s]+:") + "|" + this.MULTILINE_COMMENT_REGEX.format(" " + this.ASTERISK_REGEX + " [\\w\\s]+:[\\s\\S]*?")

    this.testCaseInfoElementsRegex = "(?<{1}>{2}TESTCASE NUMBER:{3}*?\\n)";
    this.testCaseInfoRegex = this.TEST_CASE_CODE_REGEX + "(?<{2}>(?:" + this.directiveRegex + ")|$)";
    this.testCaseInfoSingleLineRegex = this.SINGLE_LINE_COMMENT_REGEX.format(
        this.testCaseInfoElementsRegex.format("infoElementsSL", "", "\\s*.")
    ) + this.testCaseInfoRegex.format("codeSL", "nextDirectiveSL");
    this.testCaseInfoMultilineRegex = this.MULTILINE_COMMENT_REGEX.format(
        this.testCaseInfoElementsRegex.format("infoElementsML", " " + this.ASTERISK_REGEX + " ", "[\\s\\S]")
    ) + this.testCaseInfoRegex.format("codeML", "nextDirectiveML");

    this.testCaseInfoPattern = new RegExp(
        "(?:{1})|(?:{2})".format(this.testCaseInfoSingleLineRegex, this.testCaseInfoMultilineRegex)
    );
    this.testInfoElementPattern = new RegExp("(?: \\* )?(?<name>[A-Z ]+?)(?::\\s*(?<value>.*?))?\\n", "g");

    this.FILENAME_REGEX = "(?<sentenceNumber>[1-9]\\d*)\.(?<testNumber>[1-9]\\d*\.kt"
    this.SECTIONS_IN_FILE_REGEX = "[\\w-]+(,\\s+[\\w-]+)*"

    this.testTypeRegex = "(?<testType>POSITIVE|NEGATIVE)"
    this.testAreaRegex = "(?<testArea>PSI|DIAGNOSTICS|CODEGEN_BOX)"
    this.testPathStartingParagraphRegexp = new RegExp("^p-[1-9]\\d*/(pos|neg)/[1-9]\\d*.[1-9]\\d*.kt$")
    this.testPathBaseRegexTemplate = "^.*?/(?<testArea>diagnostics|psi|(?:codegen/box))/{1}"
    this.testPathRegexTemplate = this.testPathBaseRegexTemplate + "/(?<testType>pos|neg)/{1}$"
    this.sectionsInPathRegex = "(?<sections>(?:[\\w-]+)(?:/[\\w-]+)*?)"
    this.pathPartRegex = "linked/" + this.sectionsInPathRegex + "/p-(?<paragraphNumber>[1-9]\\d*)"
    this.testPathPattern = this.testPathRegexTemplate.format(this.pathPartRegex, this.FILENAME_REGEX)
    this.testInfoPattern = new RegExp(
        this.MULTILINE_COMMENT_REGEX.format(
            "{1} KOTLIN {2} SPEC TEST \\({3}\\)\\n(?<infoElements>[\\s\\S]*?\\n)".format(
                this.ASTERISK_REGEX,
                this.testAreaRegex,
                this.testTypeRegex
            )
        )
    )
    this.placePattern = "(?<sections>" + this.SECTIONS_IN_FILE_REGEX + ") -> paragraph (?<paragraphNumber>[1-9]\\d*) -> sentence (?<sentenceNumber>[1-9]\\d*)"
    this.relevantPlacesPattern = "(( " + this.ASTERISK_REGEX + " )?\\s*((?<sections>" + this.SECTIONS_IN_FILE_REGEX + ") -> )?(paragraph (?<paragraphNumber>[1-9]\\d*) -> )?sentence (?<sentenceNumber>[1-9]\\d*))+"

    this.diagnosticTagRegexp = /(<!>)|(<!(.+?(\\(\".*?\"\\))?(,\\s*)?)+?!>)/g

    this.testsByParagraph = "(\\n|^)(?<paragraphNumber>[1-9]\\d*)\\n\\s{4}(neg: (?<negTests>.*?)\\n)?(\\s{4})?(pos: (?<posTests>.*?)\\n)?";

    this.parseTestInfoElements = function(data) {
        var matches;
        var testInfoElements = {};

        while (matches = this.testInfoElementPattern.exec(data)) {
            testInfoElements[matches.groups.name] = matches.groups.value;
        }

        return testInfoElements;
    }

    this.parseTest = function(data) {
        var testMatches = this.testInfoPattern.exec(data);
        var testInfoElements = this.parseTestInfoElements(testMatches.groups.infoElements);
        var test = {
            cases: [],
            description: testInfoElements["DESCRIPTION"],
            helpers: testInfoElements["HELPERS"] ? testInfoElements["HELPERS"].split(", ") : null
        };
        var testCaseMatches = this.testCaseInfoPattern.exec(data);
        var startPosition = 0;

        while (testCaseMatches != null) {
            var infoElements = testCaseMatches.groups.infoElementsML || testCaseMatches.groups.infoElementsSL;
            var code = (testCaseMatches.groups.codeML || testCaseMatches.groups.codeSL).replace(this.diagnosticTagRegexp, "");

            startPosition += testCaseMatches.index + 1;
            test.cases.push({
                infoElements: this.parseTestInfoElements(infoElements),
                code: code
            });
            testCaseMatches = this.testCaseInfoPattern.exec(data.substr(startPosition));
        }

        return test;
    }
})()
