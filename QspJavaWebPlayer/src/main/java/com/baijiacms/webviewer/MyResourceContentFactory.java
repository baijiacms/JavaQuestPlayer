package com.baijiacms.webviewer;


import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpContent.ContentFactory;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.ResourceHttpContent;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class MyResourceContentFactory implements ContentFactory
{
    private final ResourceFactory _factory;
    private final MimeTypes _mimeTypes;
    private final CompressedContentFormat[] _precompressedFormats;

    public MyResourceContentFactory(ResourceFactory factory, MimeTypes mimeTypes, CompressedContentFormat[] precompressedFormats)
    {
        _factory = factory;
        _mimeTypes = mimeTypes;
        _precompressedFormats = precompressedFormats;
    }

    @Override
    public HttpContent getContent(String pathInContext, int maxBufferSize)
            throws IOException
    {
        try
        {
            // try loading the content from our factory.
            Resource resource = _factory.getResource(pathInContext);
            HttpContent loaded = load(pathInContext, resource, maxBufferSize);
            return loaded;
        }
        catch (Throwable t)
        {
            // Any error has potential to reveal fully qualified path
            throw (InvalidPathException)new InvalidPathException(pathInContext, "Invalid PathInContext").initCause(t);
        }
    }

    private HttpContent load(String pathInContext, Resource resource, int maxBufferSize)
            throws IOException
    {
        if (resource == null || !resource.exists())
            return null;

        if (resource.isDirectory())
            return new ResourceHttpContent(resource, _mimeTypes.getMimeByExtension(resource.toString()), maxBufferSize);

        // Look for a precompressed resource or content
        String mt = _mimeTypes.getMimeByExtension(pathInContext);
        if (_precompressedFormats.length > 0)
        {
            // Is there a compressed resource?
            Map<CompressedContentFormat, HttpContent> compressedContents = new HashMap(_precompressedFormats.length);
            for (CompressedContentFormat format : _precompressedFormats)
            {
                String compressedPathInContext = pathInContext + format.getExtension();
                Resource compressedResource = _factory.getResource(compressedPathInContext);
                if (compressedResource != null && compressedResource.exists() && compressedResource.lastModified() >= resource.lastModified() &&
                        compressedResource.length() < resource.length())
                    compressedContents.put(format,
                            new ResourceHttpContent(compressedResource, _mimeTypes.getMimeByExtension(compressedPathInContext), maxBufferSize));
            }
            if (!compressedContents.isEmpty())
                return new ResourceHttpContent(resource, mt, maxBufferSize, compressedContents);
        }
        return new ResourceHttpContent(resource, mt, maxBufferSize);
    }

    @Override
    public String toString()
    {
        return "MyResourceContentFactory[" + _factory + "]@" + hashCode();
    }
}

