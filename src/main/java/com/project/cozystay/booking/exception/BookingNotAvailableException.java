package com.project.cozystay.booking.exception;

// 409 Conflict
// 막힌 날짜 포함 + 예약 불가
public class BookingNotAvailableException extends RuntimeException {

    public BookingNotAvailableException(String message){
        super(message);
    }

    public static BookingNotAvailableException blockedDate(){
        return new BookingNotAvailableException("선택한 기간에 예약 불가능한 날짜가 포함되어 있습니다.");
    }

    public static BookingNotAvailableException minNights(int minNights){
        return new BookingNotAvailableException("최소 숙박일 존건을 만족하지 않습니다. minNights=" + minNights);
    }

    public static BookingNotAvailableException guestExceed(int requested, int max){
        return new BookingNotAvailableException(
                String.format("숙소 최대 인원 초과입니다. requested=%d, max=%d", requested, max)
        );
    }
}
