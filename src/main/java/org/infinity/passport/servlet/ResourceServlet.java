package org.infinity.passport.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceServlet extends HttpServlet {

    private static final long   serialVersionUID        = 4987172274833328015L;

    private static final Logger LOGGER                  = LoggerFactory.getLogger(ResourceServlet.class);

    public static final String  PARAM_NAME_INDEX        = "index";

    public static final String  PARAM_NAME_CONTEXT_PATH = "contextPath";

    protected final String      resourcePath;

    protected String            contextPath;

    protected String            index                   = "index.html";

    public ResourceServlet(String resourcePath, String contextPath) {
        this.resourcePath = resourcePath;
        this.contextPath = contextPath;
    }

    @Override
    public void init() throws ServletException {
        String paramIndex = getInitParameter(PARAM_NAME_INDEX);
        if (StringUtils.isNotEmpty(paramIndex)) {
            this.index = paramIndex;
        }
    }

    protected String getFilePath(String fileName) {
        return resourcePath + fileName;
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("File name: {}", fileName);
        LOGGER.debug("URI: {}", uri);
        String filePath = getFilePath(fileName);
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        if (fileName.endsWith(".jpg")) {
            byte[] bytes = IOUtils.toByteArray(is);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
            return;
        }

        if (is == null) {
            LOGGER.warn("File {} not exist", fileName);
            return;
        }
        String text = IOUtils.toString(is, StandardCharsets.UTF_8);
        if (text == null) {
            response.sendRedirect(uri + "/" + index);
            return;
        }
        if (filePath.endsWith(".html")) {
            response.setContentType("text/html;charset=utf-8");
        } else if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        if ("".equals(path)) {
            if (contextPath.equals("") || contextPath.equals("/")) {
                response.sendRedirect(MessageFormat.format("/{0}/{1}", this.contextPath, this.index));
            } else {
                response.sendRedirect(MessageFormat.format("{0}/{1}", this.contextPath, this.index));
            }
            return;
        }

        if ("/".equals(path)) {
            response.sendRedirect(index);
            return;
        }

        // find file in resources path
        returnResourceFile(path, uri, response);
    }
}
