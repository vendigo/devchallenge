package com.github.vendigo.callcenter.call;

import java.util.List;

public interface CallService {
    CallResponse handleCall(List<String> areas);
}
