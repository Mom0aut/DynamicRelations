package at.drm.service;

import at.drm.exception.NoRelationDaoFoundException;
import at.drm.model.Relation;
import at.drm.model.RelationIdentity;
import at.drm.model.RelationLink;
import at.drm.model.TreeNodeRelationIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicRelationsPrintService {

    private final EntityManager entityManager;
    private final RelationService relationService;

    private Map<String, Class<RelationIdentity>> classBySimpleName;

    public String printRelations(RelationIdentity relationIdentity) {
        TreeNodeRelationIdentity root = new TreeNodeRelationIdentity(relationIdentity, new LinkedList<>());
        createTree(root);
        return printTree(root);
    }

    private String printTree(TreeNodeRelationIdentity root) {
        Deque<Map.Entry<TreeNodeRelationIdentity, Integer>> stack = new ArrayDeque<>();
        stack.push(Map.entry(root, 0));

        StringBuilder result = new StringBuilder();
        while (!stack.isEmpty()) {
            var entry = stack.pop();
            var node = entry.getKey();
            int level = entry.getValue();

            result.append(" ".repeat(level))
                .append(node.object().getType())
                .append("\n");

            for (var child : node.childObjects()) {
                stack.push(Map.entry(child, level + 1));
            }
        }
        return result.toString();
    }

    private void createTree(TreeNodeRelationIdentity root) {
        Set<Relation> visited = new HashSet<>();
        Deque<TreeNodeRelationIdentity> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            var parentObject = stack.poll();
            Collection<RelationLink> relations = tryGetSourceObjectRelations(parentObject);
            TreeNodeRelationIdentity childObject;
            for (RelationLink relation : relations) {
                var entityClass = classBySimpleName.get(relation.getTargetType());
                var childRelationIdentity = entityManager.find(entityClass, relation.getTargetId());
                childObject = new TreeNodeRelationIdentity(childRelationIdentity, new LinkedList<>());
                var r = new Relation(parentObject.object(), childObject.object());
                if (!visited.contains(r)) {
                    parentObject.childObjects().add(childObject);
                    visited.add(r);
                    stack.add(childObject);
                }
            }
        }
    }

    @PostConstruct
    public void configure() {
        Set<Class<RelationIdentity>> classesImplementingInterface = findClassesImplementingInterface();
        classBySimpleName = classesImplementingInterface.stream()
            .collect(Collectors.toMap(clazz -> clazz.getSimpleName() + "Type", Function.identity()));
    }


    private <T> Set<Class<T>> findClassesImplementingInterface() {
        Set<Class<T>> classes = new HashSet<>();

        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AssignableTypeFilter(RelationIdentity.class));
        Set<BeanDefinition> candidates = scanner.findCandidateComponents("*");
        for (BeanDefinition candidate : candidates) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(candidate.getBeanClassName());
                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                log.error("Class not found: {}", candidate.getBeanClassName());
                throw new RuntimeException("Class is not found. Finding classes is ended");
            }
        }

        return classes;
    }

    private Collection<RelationLink> tryGetSourceObjectRelations(TreeNodeRelationIdentity root) {
        try {
            return relationService.findRelationBySourceObject(root.object());
        } catch (NoRelationDaoFoundException e) {
            log.info("No relations found for root with id {} and type {}", root.object().getId(), root.object().getType());
            return List.of();
        }
    }

}
