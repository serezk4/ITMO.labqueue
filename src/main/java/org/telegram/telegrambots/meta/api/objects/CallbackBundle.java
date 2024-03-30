package org.telegram.telegrambots.meta.api.objects;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
public class CallbackBundle {
    private final List<String> link;
    private final List<String> data;

    public CallbackBundle(List<String> link, List<String> data) {
        this.link = new ArrayList<>(link);
        this.data = new ArrayList<>(data);
    }

    public List<String> link() {
        return link;
    }

    public List<String> data() {
        return data;
    }

    public static CallbackBundle empty() {
        return new CallbackBundle(Collections.emptyList(), Collections.emptyList());
    }

    public String toCallback() {
        return link.stream().map(Object::toString).reduce((a, b) -> a + Delimiter.DATA + b).orElse("") +
                Delimiter.SERVICE +
                data.stream().map(Object::toString).reduce((a, b) -> a + Delimiter.DATA + b).orElse("");
    }

    public static CallbackBundle fromCallback(String raw) {
        String[] args = raw.split(Delimiter.SERVICE, 2);

        if (args.length > 2) log.warn("Callback has more than 2 parts, ignoring the rest");
        if (args.length < 2) log.warn("Callback has less than 2 parts, the second part will be empty");
        if (args.length == 0) {
            log.warn("Callback has no parts, returning empty list");
            return CallbackBundle.empty();
        }

        List<String> info = new ArrayList<>(Arrays.stream(args[0].split(Delimiter.DATA)).toList());
        List<String> data = new ArrayList<>(args.length > 1 ? Arrays.stream(args[1].split(Delimiter.DATA)).toList() : Collections.emptyList());

        return new CallbackBundle(info, data);
    }
}
