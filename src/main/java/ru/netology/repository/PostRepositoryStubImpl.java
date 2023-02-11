package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryStubImpl implements PostRepository {
    private final AtomicLong postID;
    private final ConcurrentHashMap<Long, Post> posts;

    public PostRepositoryStubImpl() {
        postID = new AtomicLong(0);
        posts = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        } else {
            Collection<Post> values = posts.values();
            return values.stream()
                    .filter(x -> !x.isRemoved())
                    .collect(Collectors.toList());
        }
    }
    public Optional<Post> getById(long id) {
        if (!posts.get(id).isRemoved()) {
            try {
                return Optional.ofNullable(posts.get(id));
            } catch (Exception exception) {
                throw new NotFoundException(exception);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        long postExistingID = post.getId();
        if (postExistingID > 0 && posts.containsKey(postExistingID)) {
            if (!posts.get(postExistingID).isRemoved()) {
                posts.replace(postExistingID, post);
            } else {
                throw new NotFoundException("Элемент не найден");
            }
        } else {
            long newPostId = postExistingID == 0 ? postID.incrementAndGet() : postExistingID;
            post.setId(newPostId);
            posts.put(newPostId, post);
        }
        return post;
    }

    public void removeById(long id) {
        if (!posts.get(id).isRemoved()) {
            try {
                posts.get(id).setRemoved(true);
                posts.get(id);
            } catch (Exception exception) {
                throw new NotFoundException(exception);
            }
        } else {
            throw new NotFoundException("Элемент удален");
        }
    }
}