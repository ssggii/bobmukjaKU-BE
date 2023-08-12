package bobmukjaku.bobmukjakuDemo;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {

    // 생성 시간
    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    // 수정 시간
    @LastModifiedDate
    @Column(updatable = true)
    protected LocalDateTime lastModifiedAt;

}
