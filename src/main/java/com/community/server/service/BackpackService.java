package com.community.server.service;

import com.community.server.body.BuyBody;
import com.community.server.dto.BackpackDto;
import com.community.server.dto.CompanyDto;
import com.community.server.dto.StockDto;
import com.community.server.entity.BackPackEntity;
import com.community.server.entity.CompanyEntity;
import com.community.server.entity.StockEntity;
import com.community.server.entity.UserEntity;
import com.community.server.repository.BackPackRepository;
import com.community.server.repository.CompanyRepository;
import com.community.server.repository.StockRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import com.community.server.utils.StockUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BackpackService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;
    private final BackPackRepository backPackRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;

    public BackpackService(UserRepository userRepository, CompanyRepository companyRepository, StockRepository stockRepository, BackPackRepository backPackRepository, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.stockRepository = stockRepository;
        this.backPackRepository = backPackRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseEntity<?> getBackpack(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        BackpackDto backpackDto = new BackpackDto();
        backpackDto.setBrokerage(userEntity.getBrokerage());
        backpackDto.setBalance(BigDecimal.ZERO);

        List<CompanyDto> companyList = new ArrayList<>();
        List<BackPackEntity> list = backPackRepository.findByUserId(userId);
        for (BackPackEntity backPackEntity : list) {

            if (backPackEntity.getCount() == 0){
                continue;
            }

            CompanyEntity companyEntity = companyRepository.findById(backPackEntity.getCompanyId()).orElse(null);
            if (companyEntity == null) {
                continue;
            }
            List<StockEntity> stocks = stockRepository.findByCompanyId(companyEntity.getId());

            StockUtils stockUtils = new StockUtils();
            StockDto stockDto = stockUtils.getStock(stocks);

            backpackDto.setBalance(backpackDto.getBalance().add(stockDto.getCost().multiply(BigDecimal.valueOf(backPackEntity.getCount()))));

            CompanyDto companyDto = new CompanyDto();
            companyDto.setCountHave(backPackEntity.getCount());
            companyDto.setStocks(stocks);
            companyDto.setId(companyEntity.getId());
            companyDto.setName(companyEntity.getName());
            companyDto.setCost(stockDto.getCost());
            companyDto.setProcent(stockDto.getProcent());
            companyDto.setUpper(stockDto.isUpper());
            companyDto.setStock(companyEntity.getStock());
            companyDto.setMuch(stockDto.getMuch());
            companyDto.setOwner(companyEntity.getOwner());
            companyDto.setDescription(companyEntity.getDescription());

            companyList.add(companyDto);
        }
        backpackDto.setCompanyList(companyList);
        return ResponseEntity.ok(backpackDto);
    }

    public ResponseEntity<?> getDividend(HttpServletRequest httpServletRequest) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        if (userEntity.getLastDividend() != null && userEntity.getLastDividend().after(new Date())){
            return ResponseEntity.badRequest().body("You have already received payments!");
        }

        List<BackPackEntity> list = backPackRepository.findByUserId(userId);
        for (BackPackEntity backPackEntity : list){

            List<StockEntity> stockEntities = stockRepository.findByCompanyId(backPackEntity.getCompanyId());
            StockUtils stockUtils = new StockUtils();
            StockDto stockDto = stockUtils.getStock(stockEntities);

            userEntity.setBrokerage(userEntity.getBrokerage().add(stockDto.getCost().multiply(BigDecimal.valueOf(backPackEntity.getCount()))));
        }

        Date date = new Date(new Date().getTime() + 3*24*360*1000);
        userEntity.setLastDividend(date);

        userRepository.save(userEntity);
        return ResponseEntity.ok("Dividend payout!");
    }

    public ResponseEntity<?> transfer(HttpServletRequest httpServletRequest) {

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (userEntity.getBrokerage().compareTo(BigDecimal.ZERO) == 0){
            return ResponseEntity.badRequest().body("Your brokerage account is empty!");
        }

        userEntity.setBalance(userEntity.getBalance().add(userEntity.getBrokerage()));
        userEntity.setBrokerage(BigDecimal.ZERO);

        userRepository.save(userEntity);
        return ResponseEntity.ok("Transfer done!");
    }
}
