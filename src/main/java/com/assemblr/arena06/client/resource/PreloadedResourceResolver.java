/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.resource;

import com.assemblr.arena06.common.resource.ResourceBlock;
import com.assemblr.arena06.common.resource.ResourceResolver;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author Henry
 */
public class PreloadedResourceResolver extends ResourceResolver {
    private Image defaultResouce;
    private Map<String, Image> resourceMap = new HashMap<String, Image>();
    
    public void loadResources(ResourceBlock resources) throws IOException {
        for (String s : resources.getResources()) {
            resourceMap.put(s, ImageIO.read(getClass().getResourceAsStream(s)));
        }
    }
    
    public void unloadResources(ResourceBlock resources) {
        for (String s : resources.getResources()) {
            resourceMap.remove(s);
        }
    }
    
    @Override
    public Image resolveResource(String resourceLocation) {
        Image resource = resourceMap.get(resourceLocation);
        if (resource != null) {
            return resource;
        } else {
            return defaultResouce;
        }
    }
    
}
