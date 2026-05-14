package com.project.cozystay.global.init;

import com.project.cozystay.accommodation.domain.*;
import com.project.cozystay.accommodation.repository.AccommodationAmenityRepository;
import com.project.cozystay.accommodation.repository.AccommodationImageRepository;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.accommodation.repository.AmenityRepository;
import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.chat.domain.Conversation;
import com.project.cozystay.chat.domain.Message;
import com.project.cozystay.chat.repository.ConversationRepository;
import com.project.cozystay.chat.repository.MessageRepository;
import com.project.cozystay.favorite.domain.Favorite;
import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import com.project.cozystay.favorite.repository.FavoriteAccommodationRepository;
import com.project.cozystay.favorite.repository.FavoriteRepository;
import com.project.cozystay.review.domain.AccommodationReview;
import com.project.cozystay.review.domain.UserReview;
import com.project.cozystay.review.dto.AccommodationReviewCreateRequest;
import com.project.cozystay.review.repository.AccommodationReviewRepository;
import com.project.cozystay.review.repository.UserReviewRepository;
import com.project.cozystay.user.domain.*;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final AmenityRepository amenityRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationAmenityRepository accommodationAmenityRepository;
    private final BookingRepository bookingRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final UserReviewRepository userReviewRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteAccommodationRepository favoriteAccommodationRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Dummy data already exists. Skipping initialization.");
            return;
        }

        log.info("Starting dummy data initialization...");

        // 1. Amenities 생성
        List<Amenity> amenities = createAmenities();

        // 2. Users 생성 (Guest 25, Host 25)
        List<User> guests = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            User guest = User.builder()
                    .username("guest" + i)
                    .password(passwordEncoder.encode("password"))
                    .email("guest" + i + "@example.com")
                    .nickName("Guest " + i)
                    .userRole(Role.USER)
                    .provider(AuthProvider.LOCAL)
                    .providerId("local_guest_" + i)
                    .userGrade(UserGrade.BRONZE)
                    .isEmailVerified(true)
                    .totalCompletedBookings(0)
                    .totalStayedNights(0)
                    .reviewCount(0)
                    .build();
            guests.add(userRepository.save(guest));
        }

        List<User> hosts = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            User host = User.builder()
                    .username("host" + i)
                    .password(passwordEncoder.encode("password"))
                    .email("host" + i + "@example.com")
                    .nickName("Host " + i)
                    .userRole(Role.HOST)
                    .provider(AuthProvider.LOCAL)
                    .providerId("local_host_" + i)
                    .userGrade(UserGrade.BRONZE)
                    .isEmailVerified(true)
                    .totalCompletedBookings(0)
                    .totalStayedNights(0)
                    .reviewCount(0)
                    .build();
            hosts.add(userRepository.save(host));
        }

        // 3. Accommodations 생성 (50개)
        List<Accommodation> accommodations = new ArrayList<>();
        AccommodationType[] types = AccommodationType.values();

        for (int i = 1; i <= 50; i++) {
            User host = hosts.get(random.nextInt(hosts.size()));
            String[] states = {"서울", "경기", "강원", "부산", "제주", "전남", "경북"};
            String[] cities = {"강남구", "성남시", "강릉시", "해운대구", "제주시", "여수시", "경주시"};
            String[] districts = {"신사동", "분당구", "교동", "우동", "연동", "학동", "황남동"};

            int randomIndex = random.nextInt(states.length);
            String state = states[randomIndex];
            String city = cities[randomIndex];
            String district = districts[randomIndex];

            AccommodationType type = types[random.nextInt(types.length)];
            String typeName = switch(type) {
                case ENTIRE_PLACE -> "독채 숙소";
                case PRIVATE_ROOM -> "개인실";
                case SHARED_ROOM -> "다인실";
                default -> "편안한 숙소";
            };

            Accommodation accommodation = Accommodation.builder()
                    .hostId(host.getId())
                    .title(city + " " + district + "의 포근한 " + typeName + " " + i)
                    .description(state + " " + city + " " + district + "에 위치한 아름다운 " + typeName + "입니다. 여행객분들을 위한 완벽한 휴식처가 될 것입니다!")
                    .accommodationType(type)
                    .address(state + " " + city + " " + district + " 상세주소 " + i + "번지")
                    .state(state)
                    .city(city)
                    .district(district)
                    .country("대한민국")
                    .maxGuests(random.nextInt(4) + 2)
                    .pricePerNight(BigDecimal.valueOf(50000 + random.nextInt(20) * 10000))
                    .cleaningFee(BigDecimal.valueOf(10000))
                    .serviceFeePercentage(BigDecimal.valueOf(5))
                    .instantBooking(random.nextBoolean())
                    .checkInTime(LocalTime.of(15, 0))
                    .checkOutTime(LocalTime.of(11, 0))
                    .status(AccommodationStatus.ACTIVE)
                    .build();

            // Detail 생성
            AccommodationDetail detail = AccommodationDetail.builder()
                    .roomCount(random.nextInt(3) + 1)
                    .bedroomCount(random.nextInt(2) + 1)
                    .bedCount(random.nextInt(3) + 1)
                    .bathroomCount(random.nextInt(2) + 1)
                    .wifiAvailable(true)
                    .parkingAvailable(random.nextBoolean())
                    .airConditionerCount(random.nextInt(2) + 1)
                    .hairDryerCount(1)
                    .refrigeratorCount(1)
                    .televisionCount(1)
                    .washerCount(1)
                    .dryerCount(1)
                    .build();
            
            accommodation.addDetail(detail);

            // Images 생성 (3~5개)
            int imageCount = random.nextInt(3) + 3;
            for (int j = 1; j <= imageCount; j++) {
                AccommodationImage image = AccommodationImage.builder()
                        .imageUrl("https://picsum.photos/seed/acc_" + i + "_" + j + "/800/600")
                        .primary(j == 1)
                        .displayOrder(j)
                        .build();
                accommodation.addImage(image);
            }

            // Amenities 연결 (중복 방지를 위해 Set 사용)
            Set<Amenity> selectedAmenities = new HashSet<>();
            int amenCount = random.nextInt(6) + 5; // 5~10개
            while (selectedAmenities.size() < amenCount) {
                selectedAmenities.add(amenities.get(random.nextInt(amenities.size())));
            }
            
            for (Amenity amenity : selectedAmenities) {
                AccommodationAmenity accAmenity = AccommodationAmenity.builder()
                        .accommodation(accommodation)
                        .amenity(amenity)
                        .build();
                accommodation.addAmenity(accAmenity);
            }

            accommodations.add(accommodationRepository.save(accommodation));
        }

        // 4. Bookings, Reviews, Chats, Favorites 생성
        for (User guest : guests) {
            // 각 게스트당 2~4개의 예약 생성
            int bookingCount = random.nextInt(3) + 2;
            for (int i = 0; i < bookingCount; i++) {
                Accommodation acc = accommodations.get(random.nextInt(accommodations.size()));
                LocalDate checkIn = LocalDate.now().plusDays(random.nextInt(60) - 30);
                LocalDate checkOut = checkIn.plusDays(random.nextInt(5) + 1);

                BookingStatus status;
                if (checkOut.isBefore(LocalDate.now())) {
                    status = BookingStatus.COMPLETED;
                } else if (checkIn.isAfter(LocalDate.now())) {
                    status = random.nextBoolean() ? BookingStatus.CONFIRMED : BookingStatus.PENDING;
                } else {
                    status = BookingStatus.CONFIRMED;
                }

                Booking booking = Booking.builder()
                        .accommodation(acc)
                        .guestId(guest.getId())
                        .checkInDate(checkIn)
                        .checkOutDate(checkOut)
                        .numberOfGuests(random.nextInt(acc.getMaxGuests()) + 1)
                        .totalPrice(acc.getPricePerNight().multiply(BigDecimal.valueOf(Math.max(1, checkIn.until(checkOut).getDays()))))
                        .status(status)
                        .pricePerNightSnapshot(acc.getPricePerNight())
                        .cleaningFeeSnapshot(acc.getCleaningFee())
                        .serviceFeeSnapshot(acc.getServiceFeePercentage())
                        .currency("KRW")
                        .build();

                booking = bookingRepository.save(booking);

                // COMPLETED인 경우 리뷰 생성 (50% 확률)
                if (status == BookingStatus.COMPLETED && random.nextBoolean()) {
                    AccommodationReviewCreateRequest request = new AccommodationReviewCreateRequest(
                            acc.getId(),
                            booking.getId(),
                            BigDecimal.valueOf(5),
                            BigDecimal.valueOf(5),
                            BigDecimal.valueOf(5),
                            BigDecimal.valueOf(5),
                            BigDecimal.valueOf(5),
                            "정말 멋진 숙소였어요! 편안하게 잘 쉬다 갑니다."
                    );
                    
                    AccommodationReview review = AccommodationReview.of(
                            booking,
                            acc,
                            guest,
                            request,
                            BigDecimal.valueOf(random.nextInt(2) + 4)
                    );
                    accommodationReviewRepository.save(review);

                    // 호스트도 게스트 리뷰 (30% 확률)
                    if (random.nextInt(10) < 3) {
                        User host = userRepository.findById(acc.getHostId()).orElseThrow();
                        UserReview userReview = UserReview.of(
                                booking,
                                host,
                                guest,
                                BigDecimal.valueOf(5),
                                "매너가 아주 좋으신 게스트분이셨습니다. 깨끗하게 사용해주셔서 감사합니다."
                        );
                        userReviewRepository.save(userReview);
                    }
                }

                // Chat 생성 (예약당 하나씩, 70% 확률)
                if (random.nextInt(10) < 7) {
                    User host = userRepository.findById(acc.getHostId()).orElseThrow();
                    Conversation conversation = Conversation.create(acc.getId(), host.getId(), guest.getId());
                    conversation = conversationRepository.save(conversation);

                    Message msg1 = Message.create(conversation.getId(), guest.getId(), "안녕하세요, 숙소 관련해서 문의드리고 싶습니다.");
                    messageRepository.save(msg1);
                    Message msg2 = Message.create(conversation.getId(), host.getId(), "안녕하세요! 무엇이든 편하게 물어보세요.");
                    messageRepository.save(msg2);
                }
            }

            // Favorites 생성 (각 게스트당 1~2개 폴더, 각 폴더당 3~5개 숙소)
            int favFolderCount = random.nextInt(2) + 1;
            for (int f = 1; f <= favFolderCount; f++) {
                Favorite favorite = Favorite.builder()
                        .name("나의 위시리스트 " + f)
                        .user(guest)
                        .isPrivate(random.nextBoolean())
                        .build();
                favorite = favoriteRepository.save(favorite);

                int favAccCount = random.nextInt(3) + 3;
                Set<Long> addedAccIds = new HashSet<>();
                for (int a = 0; a < favAccCount; a++) {
                    Accommodation acc = accommodations.get(random.nextInt(accommodations.size()));
                    if (addedAccIds.contains(acc.getId())) continue;
                    
                    FavoriteAccommodation favAcc = FavoriteAccommodation.of(favorite, acc);
                    favoriteAccommodationRepository.save(favAcc);
                    addedAccIds.add(acc.getId());
                }
            }
        }

        log.info("Dummy data initialization completed successfully.");
    }

    private List<Amenity> createAmenities() {
        String[][] amenityData = {
                {"무선 인터넷", "wifi", "필수 품목"},
                {"주방", "kitchen", "필수 품목"},
                {"에어컨", "ac", "필수 품목"},
                {"업무 전용 공간", "work", "필수 품목"},
                {"TV", "tv", "엔터테인먼트"},
                {"세탁기", "washer", "세탁"},
                {"무료 주차", "parking", "교통"},
                {"수영장", "pool", "럭셔리"},
                {"욕조", "hottub", "럭셔리"},
                {"아기 침대", "crib", "가족"}
        };

        List<Amenity> list = new ArrayList<>();
        for (String[] data : amenityData) {
            Amenity amenity = Amenity.builder()
                    .name(data[0])
                    .icon(data[1])
                    .category(data[2])
                    .build();
            list.add(amenityRepository.save(amenity));
        }
        return list;
    }
}
