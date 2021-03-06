package com.paninicms.views;

import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mongodb.client.model.Filters;
import com.paninicms.Panini;
import com.paninicms.plugin.PaniniPlugin;
import com.paninicms.plugin.event.Listener;
import com.paninicms.plugin.event.blog.ReadBlogPostEvent;
import com.paninicms.utils.RenderContext;
import com.paninicms.utils.blog.Post;

public class ReadPostView {
	public static Object render(RenderContext context) {
		try {
			List<Post> validPosts = Panini.getAllPosts(Filters.eq("slug", context.arguments()[1]));

			if (!validPosts.isEmpty()) {
				Post post = validPosts.get(0);
				ReadBlogPostEvent readPostEvent = new ReadBlogPostEvent(post, context);
				for (PaniniPlugin plugin : Panini.getPlugins()) {
					for (Listener listener : plugin.getListeners()) {
						PaniniPlugin.executeEvent(listener, readPostEvent);
					}
				}
				post = readPostEvent.getPost();
				context = readPostEvent.getContext();
				context.contextVars().put("post", post);
				PebbleTemplate template = Panini.getEngine().getTemplate("post.html");
				
				return template;
			} else {
				return null;
			}
		} catch (PebbleException e) {
			e.printStackTrace();
			return null;
		}
	}
}
