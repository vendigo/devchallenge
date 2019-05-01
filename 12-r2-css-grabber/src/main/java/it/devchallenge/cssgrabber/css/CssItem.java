package it.devchallenge.cssgrabber.css;

import java.util.Map;

import lombok.Value;

@Value
class CssItem {
    String selector;
    Map<String, String> cssValues;
}
