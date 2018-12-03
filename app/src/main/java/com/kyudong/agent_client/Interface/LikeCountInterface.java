package com.kyudong.agent_client.Interface;

import java.io.IOException;

/**
 * Created by Kyudong on 2018. 1. 4..
 */

public interface LikeCountInterface {

    String doLikeRequest(String url, String json) throws IOException;

}
