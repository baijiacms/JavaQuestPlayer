package com.baijiacms.qsp.controller;

import com.baijiacms.qsp.common.ResponseResult;
import com.baijiacms.qsp.util.MyMediaTypeFactory;
import com.baijiacms.qsp.util.ResponseUtil;
import com.qsp.player.Engine;
import com.qsp.player.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

    @GetMapping("/**")
    public void images(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String target=request.getServletPath();
        InputStream byteResultStream = null;
        byteResultStream = StreamUtils.getGameResourceInputSteam(Engine.INSTANCEOF.getLibEngine(), target);
        StreamUtils.copy(byteResultStream, response.getOutputStream());
        ResponseUtil.setContentType(response, MyMediaTypeFactory.getContentType(target));
    }
}
