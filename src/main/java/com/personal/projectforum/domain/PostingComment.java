package com.personal.projectforum.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

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

    @Setter @ManyToOne(optional = false) @JoinColumn(name = "userId") private UserAccount userAccount;
    @Setter @ManyToOne(optional = false) private Posting posting;
    @Setter @Column(nullable = false, length = 500) private String content;

    protected PostingComment() {} //It's possible to do it via lombok but since it's short just going to write it

    // Hide it with privte and open it with factory method
    private PostingComment(Posting posting,  UserAccount userAccount, String content) {
        this.userAccount = userAccount;
        this.posting = posting;
        this.content = content;
    }
    // Factory method
    public static PostingComment of(Posting posting, UserAccount userAccount, String content) {
        return new PostingComment(posting, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostingComment that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
