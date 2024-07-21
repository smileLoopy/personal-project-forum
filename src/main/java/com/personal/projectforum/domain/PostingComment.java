package com.personal.projectforum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
public class PostingComment extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Posting posting;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount;

    @Setter
    @Column(updatable = false)
    private Long parentCommentId;

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<PostingComment> childComments = new LinkedHashSet<>();

    @Setter @Column(nullable = false, length = 500) private String content;

    protected PostingComment() {} //It's possible to do it via lombok but since it's short just going to write it

    // Hide it with privte and open it with factory method
    private PostingComment(Posting posting,  UserAccount userAccount, Long parentCommentId, String content) {
        this.userAccount = userAccount;
        this.posting = posting;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }
    // Factory method
    public static PostingComment of(Posting posting, UserAccount userAccount, String content) {
        return new PostingComment(posting, userAccount, null, content);
    }

    public void addChildComment(PostingComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostingComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
