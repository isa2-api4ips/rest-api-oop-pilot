package eu.europa.ec.isa2.oop.restapi.utils;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractStringEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class EntityCollectionUtils {


    static public <T extends AbstractStringEntity> List<T> updateLabelList(List<T> inputList, List<String> labels,
                                                                           LabelCompareAndCreate<T> compareAndCreate) {
        // first remove all "deleted" label entities
        List<T> updatedList = inputList.stream().filter(labelEntity ->
                labels.contains(labelEntity.getStringValue())).collect(Collectors.toList());
        //add missing labels
        labels.forEach(label -> {
            if (!labelExist(inputList, label)) {
                updatedList.add(compareAndCreate.create(label));
            }
        });

        return updatedList;
    }


    static public <T extends AbstractStringEntity> boolean labelExist(List<T> labelEntities, String label) {
        return labelEntities.stream().filter(labelEntity -> StringUtils.equals(label, labelEntity.getStringValue())).findFirst().isPresent();
    }
}
