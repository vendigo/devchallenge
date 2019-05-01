var sheets = document.styleSheets, result = [];
for (var i in sheets) {
    if (!sheets[i].href) {
        var rules = sheets[i].cssRules;
        for (var r in rules) {
            var selectorText = rules[r].selectorText;
            var cssText = rules[r].cssText;

            if (document.querySelector(selectorText)) {
                result.push({"selector": selectorText, "cssText": cssText});
            }
        }
    }
}
return result;
