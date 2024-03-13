package ch.fhnw.swc.mrs.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utitlity class to support I18N.
 */
public class MessageBundle {

    private ResourceBundle messages;

    /**
     * Create a message bundle for the given language.
     * 
     * @param languageTag currently only "en" and "de" are supported.
     */
    public MessageBundle(String languageTag) {
        Locale locale = languageTag != null ? new Locale(languageTag) : Locale.ENGLISH;
        this.messages = ResourceBundle.getBundle("localization/messages", locale);
    }

    /**
     * Translate a message according to the selected language.
     * 
     * @param message A message place holder
     * @return the translated text.
     */
    public String get(String message) {
        return messages.getString(message);
    }

    /**
     * Get a certain text.
     * 
     * @param key the key for the text to translate
     * @param args some arguments
     * @return the translation
     */
    public final String get(final String key, final Object... args) {
        return MessageFormat.format(get(key), args);
    }

}
