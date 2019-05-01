package it.devchallenge.sharepassword.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

import it.devchallenge.sharepassword.model.ParsedFetchRequest;

public class LinkConverterServiceTest {

    private static final String ENCODED_LINK = "MS52ZXJpZmljYXRpb24uc2VjcmV0";
    private static final long PASSWORD_ID = 1L;
    private static final String VERIFICATION_CODE = "verification";
    private static final String SECRET_KEY = "secret";
    private static final String WRONG_FORMATTED_LINK = "MS5zb21ldGhpbmc=";
    private final LinkConverterService linkConverterService = new LinkConverterService();

    @Test
    public void buildLink() {
        String link = linkConverterService.buildLink(PASSWORD_ID, VERIFICATION_CODE, SECRET_KEY);
        assertThat(link, equalTo(ENCODED_LINK));
    }

    @Test
    public void parseLink() {
        ParsedFetchRequest parsedRequest = linkConverterService.parseLink(ENCODED_LINK);
        assertThat(parsedRequest, equalTo(new ParsedFetchRequest(PASSWORD_ID, VERIFICATION_CODE, SECRET_KEY)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWrongFormattedLink() {
        linkConverterService.parseLink(WRONG_FORMATTED_LINK);
    }
}
