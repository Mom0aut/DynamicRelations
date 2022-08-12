package at.drm.service;


import at.drm.dao.DynamicRelationDao;
import at.drm.factory.DynamicRelationDaoFactory;
import at.drm.model.CreateRelationInput;
import at.drm.model.DynamicRelationModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DynamicRelationService {

    private final DynamicRelationDaoFactory dynamicRelationDaoFactory;

    public DynamicRelationModel createDynamicRelation(@NonNull CreateRelationInput createRelationInput) {
        Object sourceObject = createRelationInput.sourceObject();
        Long targetId = createRelationInput.targetId();
        String targetType = createRelationInput.targetType();
        DynamicRelationDao<DynamicRelationModel, Long> daoFromSourceObjectClass =
                dynamicRelationDaoFactory.getDaoFromSourceObjectClass(sourceObject.getClass());
        DynamicRelationModel dynamicRelationModel = createDynamicRelationModelFromGenericParameter(daoFromSourceObjectClass);
        dynamicRelationModel.setSourceObject(sourceObject);
        dynamicRelationModel.setTargetId(targetId);
        dynamicRelationModel.setTargetType(targetType);
        return daoFromSourceObjectClass.save(dynamicRelationModel);
    }

    public void deleteDynamicRelation(DynamicRelationModel dynamicRelationModel) {
        DynamicRelationDao<DynamicRelationModel, Long> daoFromSourceObjectClass =
                dynamicRelationDaoFactory.getDaoFromSourceObjectClass(dynamicRelationModel.getSourceObject().getClass());
        daoFromSourceObjectClass.delete(dynamicRelationModel);
    }

    private static DynamicRelationModel createDynamicRelationModelFromGenericParameter(DynamicRelationDao<DynamicRelationModel,
            Long> daoFromSourceObjectClass) {
        ResolvableType resolvableType = ResolvableType.forClass(daoFromSourceObjectClass.getClass())
                .as(DynamicRelationDao.class);
        ResolvableType generic = resolvableType.getGeneric(0);
        Class<?> resolve = generic.resolve();
        return (DynamicRelationModel) BeanUtils.instantiateClass(resolve);
    }
}
