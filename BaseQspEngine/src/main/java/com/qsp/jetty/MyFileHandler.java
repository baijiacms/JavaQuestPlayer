package com.qsp.jetty;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.server.ResourceService.WelcomeFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFileHandler extends HandlerWrapper implements ResourceFactory, WelcomeFactory {
    private static final Logger LOG = Log.getLogger(MyFileHandler.class);

    Resource baseResource;
    ContextHandler context;
    Resource defaultStylesheet;
    MimeTypes mimeTypes;
    private final ResourceService resourceService;
    Resource stylesheet;
    String[] welcomes = {"index.html"};

    public MyFileHandler(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public MyFileHandler() {
        this(new ResourceService() {
            @Override
            protected void notFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
            }
        });
        resourceService.setGzipEquivalentFileExtensions(new ArrayList<>(Arrays.asList(".svgz")));
    }

    @Override
    public String getWelcomeFile(String pathInContext) {
        if (welcomes == null)
            return null;

        for (int i = 0; i < welcomes.length; i++) {
            String welcomeInContext = URIUtil.addPaths(pathInContext, welcomes[i]);
            Resource welcome = getResource(welcomeInContext);
            if (welcome != null && welcome.exists())
                return welcomeInContext;
        }
        // not found
        return null;
    }

    @Override
    public void doStart() throws Exception {
        Context scontext = ContextHandler.getCurrentContext();
        context = (scontext == null ? null : scontext.getContextHandler());
        if (mimeTypes == null)
            mimeTypes = context == null ? new MimeTypes() : context.getMimeTypes();

        resourceService.setContentFactory(new MyResourceContentFactory(this, mimeTypes, resourceService.getPrecompressedFormats()));
        resourceService.setWelcomeFactory(this);

        super.doStart();
    }

    /**
     * @return Returns the resourceBase.
     */
    public Resource getBaseResource() {
        if (baseResource == null)
            return null;
        return baseResource;
    }

    /**
     * @return the cacheControl header to set on all static content.
     */
    public String getCacheControl() {
        return resourceService.getCacheControl().getValue();
    }

    /**
     * @return file extensions that signify that a file is gzip compressed. Eg ".svgz"
     */
    public List<String> getGzipEquivalentFileExtensions() {
        return resourceService.getGzipEquivalentFileExtensions();
    }

    public MimeTypes getMimeTypes() {
        return mimeTypes;
    }

    /**
     * Get the minimum content length for async handling.
     *
     * @return The minimum size in bytes of the content before asynchronous handling is used, or -1 for no async handling or 0 (default) for using
     * {@link HttpServletResponse#getBufferSize()} as the minimum length.
     */
    @Deprecated
    public int getMinAsyncContentLength() {
        return -1;
    }

    /**
     * Get minimum memory mapped file content length.
     *
     * @return the minimum size in bytes of a file resource that will be served using a memory mapped buffer, or -1 (default) for no memory mapped buffers.
     */
    @Deprecated
    public int getMinMemoryMappedContentLength() {
        return -1;
    }

    @Override
    public Resource getResource(String path) {
        if (LOG.isDebugEnabled())
            LOG.debug("{} getResource({})", context == null ? baseResource : context, baseResource, path);

        if (path == null || !path.startsWith("/"))
            return null;

        try {
            Resource r = null;

            if (baseResource != null) {
                path = URIUtil.canonicalPath(path);
                r = baseResource.addPath(path);

                if (r != null && r.isAlias() && (context == null || !context.checkAlias(path, r))) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("resource={} alias={}", r, r.getAlias());
                    return null;
                }
            } else if (context != null)
                r = context.getResource(path);

            if ((r == null || !r.exists()) && path.endsWith("/jetty-dir.css"))
                r = getStylesheet();

            return r;
        } catch (Exception e) {
            LOG.debug(e);
        }

        return null;
    }

    /**
     * @return Returns the base resource as a string.
     */
    public String getResourceBase() {
        if (baseResource == null)
            return null;
        return baseResource.toString();
    }

    /**
     * @return Returns the stylesheet as a Resource.
     */
    public Resource getStylesheet() {
        if (stylesheet != null) {
            return stylesheet;
        } else {
            if (defaultStylesheet == null) {
                defaultStylesheet = getDefaultStylesheet();
            }
            return defaultStylesheet;
        }
    }

    public static Resource getDefaultStylesheet() {
        return Resource.newResource(MyFileHandler.class.getResource("/jetty-dir.css"));
    }

    public String[] getWelcomeFiles() {
        return welcomes;
    }

    /*
     * @see org.eclipse.jetty.server.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        if (baseRequest.isHandled())
//            return;

        if (!HttpMethod.GET.is(request.getMethod()) && !HttpMethod.HEAD.is(request.getMethod())) {
            // try another handler
            super.handle(target, baseRequest, request, response);
            return;
        }

        if (resourceService.doGet(request, response))
            baseRequest.setHandled(true);
        else
            // no resource - try other handlers
            super.handle(target, baseRequest, request, response);
    }

    /**
     * @return If true, range requests and responses are supported
     */
    public boolean isAcceptRanges() {
        return resourceService.isAcceptRanges();
    }

    /**
     * @return If true, directory listings are returned if no welcome file is found. Else 403 Forbidden.
     */
    public boolean isDirAllowed() {
        return resourceService.isDirAllowed();
    }

    /**
     * Get the directory option.
     *
     * @return true if directories are listed.
     */
    public boolean isDirectoriesListed() {
        return resourceService.isDirAllowed();
    }

    /**
     * @return True if ETag processing is done
     */
    public boolean isEtags() {
        return resourceService.isEtags();
    }

    /**
     * @return If set to true, then static content will be served as gzip content encoded if a matching resource is found ending with ".gz"
     */
    @Deprecated
    public boolean isGzip() {
        for (CompressedContentFormat formats : resourceService.getPrecompressedFormats()) {
            if (CompressedContentFormat.GZIP.getEncoding().equals(formats.getEncoding()))
                return true;
        }
        return false;
    }

    /**
     * @return Precompressed resources formats that can be used to serve compressed variant of resources.
     */
    public CompressedContentFormat[] getPrecompressedFormats() {
        return resourceService.getPrecompressedFormats();
    }

    /**
     * @return true, only the path info will be applied to the resourceBase
     */
    public boolean isPathInfoOnly() {
        return resourceService.isPathInfoOnly();
    }

    /**
     * @return If true, welcome files are redirected rather than forwarded to.
     */
    public boolean isRedirectWelcome() {
        return resourceService.isRedirectWelcome();
    }

    /**
     * @param acceptRanges If true, range requests and responses are supported
     */
    public void setAcceptRanges(boolean acceptRanges) {
        resourceService.setAcceptRanges(acceptRanges);
    }

    /**
     * @param base The resourceBase to server content from. If null the
     *             context resource base is used.
     */
    public void setBaseResource(Resource base) {
        baseResource = base;
    }

    /**
     * @param cacheControl the cacheControl header to set on all static content.
     */
    public void setCacheControl(String cacheControl) {
        resourceService.setCacheControl(new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cacheControl));
    }

    /**
     * @param dirAllowed If true, directory listings are returned if no welcome file is found. Else 403 Forbidden.
     */
    public void setDirAllowed(boolean dirAllowed) {
        resourceService.setDirAllowed(dirAllowed);
    }

    /**
     * Set the directory.
     *
     * @param directory true if directories are listed.
     */
    public void setDirectoriesListed(boolean directory) {
        resourceService.setDirAllowed(directory);
    }

    /**
     * @param etags True if ETag processing is done
     */
    public void setEtags(boolean etags) {
        resourceService.setEtags(etags);
    }

    /**
     * @param gzip If set to true, then static content will be served as gzip content encoded if a matching resource is found ending with ".gz"
     */
    @Deprecated
    public void setGzip(boolean gzip) {
        setPrecompressedFormats(gzip ? new CompressedContentFormat[]{
                CompressedContentFormat.GZIP
        } : new CompressedContentFormat[0]);
    }

    /**
     * @param gzipEquivalentFileExtensions file extensions that signify that a file is gzip compressed. Eg ".svgz"
     */
    public void setGzipEquivalentFileExtensions(List<String> gzipEquivalentFileExtensions) {
        resourceService.setGzipEquivalentFileExtensions(gzipEquivalentFileExtensions);
    }

    /**
     * @param precompressedFormats The list of precompresed formats to serve in encoded format if matching resource found.
     *                             For example serve gzip encoded file if ".gz" suffixed resource is found.
     */
    public void setPrecompressedFormats(CompressedContentFormat[] precompressedFormats) {
        resourceService.setPrecompressedFormats(precompressedFormats);
    }

    public void setMimeTypes(MimeTypes mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    /**
     * Set the minimum content length for async handling.
     *
     * @param minAsyncContentLength The minimum size in bytes of the content before asynchronous handling is used, or -1 for no async handling or 0 for using
     *                              {@link HttpServletResponse#getBufferSize()} as the minimum length.
     */
    @Deprecated
    public void setMinAsyncContentLength(int minAsyncContentLength) {
    }

    /**
     * Set minimum memory mapped file content length.
     *
     * @param minMemoryMappedFileSize the minimum size in bytes of a file resource that will be served using a memory mapped buffer, or -1 for no memory mapped buffers.
     */
    @Deprecated
    public void setMinMemoryMappedContentLength(int minMemoryMappedFileSize) {
    }

    /**
     * @param pathInfoOnly true, only the path info will be applied to the resourceBase
     */
    public void setPathInfoOnly(boolean pathInfoOnly) {
        resourceService.setPathInfoOnly(pathInfoOnly);
    }

    /**
     * @param redirectWelcome If true, welcome files are redirected rather than forwarded to.
     *                        redirection is always used if the MyFileHandler is not scoped by
     *                        a ContextHandler
     */
    public void setRedirectWelcome(boolean redirectWelcome) {
        resourceService.setRedirectWelcome(redirectWelcome);
    }

    /**
     * @param resourceBase The base resource as a string.
     */
    public void setResourceBase(String resourceBase) {
        try {
            setBaseResource(Resource.newResource(resourceBase));
        } catch (Exception e) {
            LOG.warn(e.toString());
            LOG.debug(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }

    /**
     * @param stylesheet The location of the stylesheet to be used as a String.
     */
    public void setStylesheet(String stylesheet) {
        try {
            this.stylesheet = Resource.newResource(stylesheet);
            if (!this.stylesheet.exists()) {
                LOG.warn("unable to find custom stylesheet: " + stylesheet);
                this.stylesheet = null;
            }
        } catch (Exception e) {
            LOG.warn(e.toString());
            LOG.debug(e);
            throw new IllegalArgumentException(stylesheet);
        }
    }

    public void setWelcomeFiles(String[] welcomeFiles) {
        welcomes = welcomeFiles;
    }
}

