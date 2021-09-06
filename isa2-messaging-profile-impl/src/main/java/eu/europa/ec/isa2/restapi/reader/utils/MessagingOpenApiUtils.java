package eu.europa.ec.isa2.restapi.reader.utils;

import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessagingOpenApiUtils {

    public static final List<Tag> getGlobalTags(Class<?> beanType) {

        List<io.swagger.v3.oas.annotations.tags.Tag> allTags = new ArrayList<>();
        List<io.swagger.v3.oas.models.tags.Tag> tags = new ArrayList<>();
        Set<String> tagsStr = new HashSet<>();

        // class tags
        Set<Tags> tagsSet = AnnotatedElementUtils
                .findAllMergedAnnotations(beanType, Tags.class);
        Set<io.swagger.v3.oas.annotations.tags.Tag> classTags = tagsSet.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toSet());
        classTags.addAll(AnnotatedElementUtils.findAllMergedAnnotations(beanType, io.swagger.v3.oas.annotations.tags.Tag.class));
        if (!CollectionUtils.isEmpty(classTags)) {
            tagsStr.addAll(classTags.stream().map(io.swagger.v3.oas.annotations.tags.Tag::name).collect(Collectors.toSet()));
            allTags.addAll(classTags);
            AnnotationsUtils
                    .getTags(allTags.toArray(new io.swagger.v3.oas.annotations.tags.Tag[0]), true).ifPresent(tags::addAll);
        }
        return tags;
    }
}
