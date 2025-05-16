package com.example.capstonedesign.api;

import com.example.capstonedesign.domain.Menu;
import com.example.capstonedesign.domain.OpeningHours;
import com.example.capstonedesign.domain.Place;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class KaKaoPlaceCrawler {
    private final String imageSavePath = "src/main/resources/static/images/";

    public Place crawlKakaoMap() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try{
            Place place = new Place();
            String url = "https://place.map.kakao.com/" + "10393075";
            driver.get(url);
            Thread.sleep(3000);

            crawlName(driver, place);
            crawlCategory(driver, place);
            crawlAddress(driver, place);
            crawlPhoneNumber(driver, place);
            crawlMenus(driver, place);
            crawlStarRate(driver, place);
            crawlImage(driver, place);
            crawlOpeningHours(driver,place);
            return place;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("스레드 interrupted");
            return null;
        }finally {
            driver.quit();
        }
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
            WebElement openingHoursInfo = driver.findElement(By.id("foldDetail2"));
            // 요일별 상세 정보 처리
            List<WebElement> dayInfoList = openingHoursInfo.findElements(By.cssSelector(".line_fold"));
            for (WebElement dayInfo : dayInfoList) {
                try {
                    String dayOfWeek = dayInfo.findElement(By.cssSelector(".tit_fold")).getText().trim();
                    System.out.println(dayOfWeek);

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
                        }
                    }
                    hoursList.add(hours);

                } catch (Exception e) {
                    System.err.println("요일별 영업시간 파싱 실패: " + e.getMessage());
                }
            }

            // "매일" 정보 처리
            try {
                WebElement dailyTimeInfo = openingHoursInfo.findElement(By.cssSelector(".detail_info .info_operation > span"));
                String dailyText = dailyTimeInfo.getText().trim();
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
                } else if (dailyText.contains("휴무일")) {
                    OpeningHours hours = new OpeningHours();
                    if (dailyText.contains("화요일")) {
                        hours.setDayOfWeek("화");
                    } else {
                        hours.setDayOfWeek("휴무일");
                    }
                    hours.setNote(dailyText);
                    hours.setPlace(place);
                    hoursList.add(hours);
                }
            } catch (Exception e) {
                System.err.println("간단 영업시간 파싱 실패: " + e.getMessage());
            }

            place.setHoursList(hoursList);

        } catch (Exception e) {
            System.err.println("영업시간 정보를 찾을 수 없음: " + e.getMessage());
        }
    }
}

