package com.logicaldoc.core.communication;

import java.util.Map;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.automation.Automation;
import com.logicaldoc.util.LocaleUtil;

/**
 * A template for messaging purposes.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.5
 */
public class MessageTemplate extends PersistentObject {

	private static final long serialVersionUID = 1L;

	public static final String TYPE_SYSTEM = "system";

	public static final String TYPE_USER = "user";

	private String name = "";

	private String description = "";

	private String language = "en";

	private String body;

	private String subject;

	private String type = TYPE_SYSTEM;

	public MessageTemplate() {
	}

	public MessageTemplate(MessageTemplate source) {
		body = source.getBody();
		description = source.getDescription();
		language = source.getLanguage();
		name = source.getName();
		subject = source.getSubject();
		setTenantId(source.getTenantId());
	}

	private String getFormattedContent(Map<String, Object> dictionary, String text) {
		Automation script = new Automation(getName(), LocaleUtil.toLocale(language), getTenantId());
		String content = script.evaluate(text, dictionary);
		if (content != null)
			content = content.trim();
		return content;
	}

	public String getFormattedBody(Map<String, Object> dictionary) {
		return getFormattedContent(dictionary, getBody());
	}

	public String getFormattedSubject(Map<String, Object> dictionary) {
		return getFormattedContent(dictionary, getSubject());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}