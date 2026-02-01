package com.project.cozystay.booking.guest.domain;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.guest.exception.BookingGuestResponseNotAllowedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "booking_guests",
indexes = {
        @Index(name = "idx_booking_guests_booking_id", columnList = "booking_id")
})
public class BookingGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_guest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // 가입자 동반자
    @Column(name = "guest_user_id")
    private Long guestUserId;

    // 비가입자 동반자
    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_email", nullable = false)
    private String guestEmail;

    @Column(name = "guest_phone", nullable = false)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    private InvitationStatus invitationStatus;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    void prePersist(){
        if(this.invitationStatus == null) this.invitationStatus = InvitationStatus.PENDING;
        if(this.invitedAt == null) this.invitedAt = LocalDateTime.now();
    }

    public static BookingGuest invite(Booking booking,
                                      Long guestUserId,
                                      String guestName,
                                      String guestEmail,
                                      String guestPhone){
        BookingGuest bg = new BookingGuest();
        bg.booking = booking;
        bg.guestUserId = guestUserId;
        bg.guestName = guestName;
        bg.guestEmail = guestEmail;
        bg.guestPhone = guestPhone;
        bg.invitationStatus = InvitationStatus.PENDING;
        bg.invitedAt = LocalDateTime.now();
        return bg;
    }


    public void accept(){
        if(this.invitationStatus != InvitationStatus.PENDING){
            throw new BookingGuestResponseNotAllowedException("PENDING 상태에서만 수락할 수 있습니다.");
        }
        this.invitationStatus= InvitationStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void decline(){
        if(this.invitationStatus != InvitationStatus.PENDING){
            throw new BookingGuestResponseNotAllowedException("PENDING 상태에서만 거절할 수 있습니다.");
        }
        this.invitationStatus = InvitationStatus.DECLINED;
        this.respondedAt = LocalDateTime.now();
    }
}
