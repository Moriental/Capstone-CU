package com.example.capstonedesign.service;

import com.example.capstonedesign.domain.Menu;
import com.example.capstonedesign.domain.OpeningHours;
import com.example.capstonedesign.domain.Place;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class KaKaoPlaceCrawler {
    private final String imageSavePath = "src/main/resources/static/images/";
    private final List<String> koreanFoodUrls = List.of(
            "https://place.map.kakao.com/10393075", // 예시 URL
            "https://place.map.kakao.com/24876101",
            "https://place.map.kakao.com/1267898551",
            "https://place.map.kakao.com/19595215",
            "https://place.map.kakao.com/2073609154",
            "https://place.map.kakao.com/34689166",
            "https://place.map.kakao.com/599002619",
            "https://place.map.kakao.com/899639875",
            "https://place.map.kakao.com/12147722",
            "https://place.map.kakao.com/1840938758"
    );
    private final List<String> japaneseFoodUrls = List.of(
            "https://place.map.kakao.com/776933867",// 예시 URL
            "https://place.map.kakao.com/35557730",
            "https://place.map.kakao.com/19839750",
            "https://place.map.kakao.com/67863765",
            "https://place.map.kakao.com/1818413544",
            "https://place.map.kakao.com/1756947472",
            "https://place.map.kakao.com/1100581290",
            "https://place.map.kakao.com/1162856010",
            "https://place.map.kakao.com/97633386",
            "https://place.map.kakao.com/627926452"
    );
    private final List<String> chineseFoodUrls = List.of(
            "https://place.map.kakao.com/1742677282",// 예시 URL
            "https://place.map.kakao.com/10172333",
            "https://place.map.kakao.com/1405122256",
            "https://place.map.kakao.com/247356503",
            "https://place.map.kakao.com/1901724501",
            "https://place.map.kakao.com/593414146",
            "https://place.map.kakao.com/2073198155",
            "https://place.map.kakao.com/1718079707",
            "https://place.map.kakao.com/523560420",
            "https://place.map.kakao.com/1907866160"
    );
    private final List<String> westernFoodUrls = List.of(
            "https://place.map.kakao.com/987087961", // 예시 URL
            "https://place.map.kakao.com/361756971",
            "https://place.map.kakao.com/833656846",
            "https://place.map.kakao.com/1990349196",
            "https://place.map.kakao.com/2089105130",
            "https://place.map.kakao.com/1569066420",
            "https://place.map.kakao.com/1365644373",
            "https://place.map.kakao.com/260625",
            "https://place.map.kakao.com/1167121505",
            "https://place.map.kakao.com/13057140"
    );

    public List<Place> crawlKoreanPlaces() {
        return crawlPlaces(koreanFoodUrls, "한식");
    }

    public List<Place> crawlJapanesePlaces() {
        return crawlPlaces(japaneseFoodUrls, "일식");
    }

    public List<Place> crawlChinesePlaces() {
        return crawlPlaces(chineseFoodUrls, "중식");
    }

    public List<Place> crawlWesternPlaces() {
        return crawlPlaces(westernFoodUrls, "양식");
    }

    public List<Place> crawlPlaces(List<String> urls, String foodType) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        List<Place> crawledPlaces = new ArrayList<>();

        try {
            for (String url : urls) {
                Place place = new Place();
                driver.get(url);
                Thread.sleep(5000);

                crawlName(driver, place);
                crawlCategory(driver, place);
                place.setFood_type(foodType);
                crawlAddress(driver, place);
                crawlPhoneNumber(driver, place);
                crawlMenus(driver, place);
                crawlStarRate(driver, place);
                crawlImage(driver, place);
                crawlOpeningHours(driver, place);

                crawledPlaces.add(place);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("스레드 interrupted");
        } finally {
            driver.quit();
        }
        return crawledPlaces;
    }

    private void crawlName(WebDriver driver, Place place) {
        try {
            WebElement nameEl = driver.findElement(By.cssSelector("h3.tit_place"));
            place.setName(nameEl.getText().trim());
        } catch (Exception e) {
            System.err.println("이름 정보 크롤링 실패: " + e.getMessage());
        }
    }

    private void crawlCategory(WebDriver driver, Place place) {
        try {
            WebElement categoryEl = driver.findElement(By.cssSelector("span.info_cate"));
            place.setCategory(categoryEl.getText().trim());
        } catch (Exception e) {
            System.err.println("카테고리 정보 크롤링 실패: " + e.getMessage());
        }
    }

    private void crawlAddress(WebDriver driver, Place place) {
        try {
            WebElement addressEl = driver.findElement(By.cssSelector(".detail_info .row_detail .txt_detail"));
            place.setAddress(addressEl.getText().trim());
            System.out.println("주소: " + place.getAddress());
        } catch (Exception e) {
            System.err.println("주소 정보 크롤링 실패: " + e.getMessage());
        }
    }

    private void crawlPhoneNumber(WebDriver driver, Place place) {
        try {
            WebElement phoneEl = driver.findElement(By.cssSelector(".detail_info.info_suggest .row_detail .txt_detail"));
            place.setPhoneNumber(phoneEl.getText().trim());
            System.out.println("전화번호 : " + place.getPhoneNumber());
        } catch (Exception e) {
            System.err.println("전화번호 정보 크롤링 실패: " + e.getMessage());
        }
    }

    private void crawlMenus(WebDriver driver, Place place) {
        List<Menu> menus = new ArrayList<>();
        try {
            WebElement menuList = driver.findElement(By.cssSelector(".list_goods"));
            List<WebElement> menuItems = menuList.findElements(By.tagName("li"));

            for (WebElement menuItem : menuItems) {
                try {
                    WebElement nameElement = menuItem.findElement(By.cssSelector(".tit_item"));
                    String name = nameElement.getText().trim();

                    WebElement priceElement = menuItem.findElement(By.cssSelector(".desc_item"));
                    String price = priceElement.getText().trim();

                    Menu menu = new Menu();
                    menu.setMenuName(name);
                    menu.setPrice(price);
                    menu.setPlace(place);
                    menus.add(menu);
                } catch (Exception e) {
                    System.err.println("메뉴 아이템 크롤링 실패: " + e.getMessage());
                }
            }
            place.setMenus(menus);
        } catch (Exception e) {
            System.err.println("메뉴 목록을 찾을 수 없음: " + e.getMessage());
        }
    }

    private void crawlStarRate(WebDriver driver, Place place) {
        try {
            WebElement starEl = driver.findElement(By.cssSelector(".link_info .starred_grade .num_star"));
            String starRateStr = starEl.getText().trim();
            double starRateDouble = Double.parseDouble(starRateStr);
            place.setStarRate(starRateDouble);
            System.out.println("별점 : " + starRateDouble);
        } catch (NumberFormatException e) {
            System.err.println("별점 형식이 올바르지 않습니다.");
        } catch (Exception e) {
            System.err.println("별점을 찾을 수 없음: " + e.getMessage());
        }
    }

    private void crawlImage(WebDriver driver, Place place) {
        try {
            WebElement imgEl = driver.findElement(By.cssSelector(".inner_board .col .link_photo img"));
            String imageUrl = imgEl.getAttribute("src");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try (InputStream in = new URL(imageUrl).openStream()) {
                    String fileName = place.getName() + ".jpg";
                    Path saveFilePath = Paths.get(imageSavePath, fileName);
                    Files.copy(in, saveFilePath, StandardCopyOption.REPLACE_EXISTING);
                    place.setImage("/images/" + fileName);
                } catch (IOException e) {
                    System.err.println("이미지 다운로드 실패: " + e.getMessage());
                    place.setImage(null);
                }
            } else {
                place.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("이미지 정보를 찾을 수 없음: " + e.getMessage());
        }
    }

    private void crawlOpeningHours(WebDriver driver, Place place) {
        List<OpeningHours> hoursList = new ArrayList<>();
        try {
            WebElement toggleButton = driver.findElement(By.cssSelector("button[type='button'][aria-controls='foldDetail2']"));
            toggleButton.click();
            WebElement openingHoursInfo = driver.findElement(By.id("foldDetail2"));

            // 1. "매일" 정보 먼저 파싱 시도
            try {
                WebElement dailyTimeElement = openingHoursInfo.findElement(By.cssSelector("#foldDetail2 .line_fold .txt_detail"));
                String dailyText = dailyTimeElement.getText().trim();

                if (dailyText.startsWith("매일")) {
                    String[] parts = dailyText.substring(2).trim().split("~");
                    if (parts.length == 2) {
                        OpeningHours hours = new OpeningHours();
                        hours.setDayOfWeek("매일");
                        hours.setOpenTime(parts[0].trim());
                        hours.setCloseTime(parts[1].trim());
                        hours.setPlace(place);
                        hoursList.add(hours);
                    }
                    place.setHoursList(hoursList);
                    return; // "매일" 정보를 파싱했으면 종료
                } else if (dailyText.contains("휴무일")) {
                    OpeningHours hours = new OpeningHours();
                    hours.setPlace(place);

                    String foundDay = null;
                    String[] daysOfWeekKeywords = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일", "월", "화", "수", "목", "금", "토", "일"};
                    String[] shortDaysOfWeek = {"월", "화", "수", "목", "금", "토", "일"}; // DB에 저장할 짧은 요일

                    for (int i = 0; i < daysOfWeekKeywords.length; i++) {
                        if (dailyText.contains(daysOfWeekKeywords[i])) {
                            foundDay = shortDaysOfWeek[i % (daysOfWeekKeywords.length / 2)];
                            break;
                        }
                    }

                    if (foundDay != null) {
                        hours.setDayOfWeek(foundDay);
                        hours.setNote("휴무일"); // 또는 dailyText 전체를 저장할 수도 있습니다.
                    } else {
                        hours.setDayOfWeek("휴무일"); // 또는 null로 두고 note에만 정보를 남길 수도 있습니다.
                        hours.setNote(dailyText);
                    }

                    hoursList.add(hours);
                    place.setHoursList(hoursList);
                    return; // 휴무일 정보를 파싱했으면 종료
                }
                // "매일"도 아니고 "휴무일" 정보도 아니면, 이 try 블록에서는 처리하지 않고
                // 아래의 요일별 상세 정보 파싱으로 넘어갑니다.

            } catch (Exception e) {
                System.err.println("간단 영업시간 파싱 실패 (요일별 정보 시도): " + e.getMessage());
                // "매일" 또는 "휴무일" 정보가 없는 경우, 아래의 요일별 정보 파싱 시도
            }

            // 2. 요일별 상세 정보 파싱 시도 (위에서 "매일" 또는 "휴무일" 정보가 없었거나, 파싱에 실패한 경우)
            List<WebElement> dayInfoList = openingHoursInfo.findElements(By.cssSelector(".line_fold"));
            for (WebElement dayInfo : dayInfoList) {
                try {
                    WebElement dayOfWeekElement = dayInfo.findElement(By.cssSelector(".tit_fold"));
                    String dayOfWeek = dayOfWeekElement.getText().trim();
                    List<WebElement> timeDetails = dayInfo.findElements(By.cssSelector(".detail_fold .txt_detail"));

                    OpeningHours hours = new OpeningHours();
                    hours.setDayOfWeek(dayOfWeek);
                    hours.setPlace(place);

                    if (timeDetails.size() >= 1) {
                        String operationTime = timeDetails.get(0).getText().trim();
                        String[] operationParts = operationTime.split("~");
                        if (operationParts.length == 2) {
                            hours.setOpenTime(operationParts[0].trim());
                            hours.setCloseTime(operationParts[1].trim());
                        }
                    }

                    if (timeDetails.size() >= 2 && timeDetails.get(1).getText().contains("브레이크타임")) {
                        String breakTime = timeDetails.get(1).getText().replace("브레이크타임", "").trim();
                        String[] breakParts = breakTime.split("~");
                        if (breakParts.length == 2) {
                            hours.setBreakStartTime(breakParts[0].trim());
                            hours.setBreakEndTime(breakParts[1].trim());
                        } else {
                            hours.setBreakStartTime("정보없음");
                            hours.setBreakEndTime("정보없음");
                        }
                    } else {
                        hours.setBreakStartTime("정보없음");
                        hours.setBreakEndTime("정보없음");
                    }
                    hoursList.add(hours);

                } catch (Exception e) {
                    System.err.println("요일별 영업시간 파싱 실패: " + e.getMessage());
                }
            }
            place.setHoursList(hoursList);

        } catch (Exception e) {
            System.err.println("영업시간 정보를 찾을 수 없음: " + e.getMessage());
        }
    }
}