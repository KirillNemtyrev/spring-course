package com.community.server.service;

import com.community.server.body.BuyBody;
import com.community.server.body.SellBody;
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
import java.util.List;

@Service
public class StockService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final BackPackRepository backPackRepository;
    private final StockRepository stockRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;

    public StockService(UserRepository userRepository, CompanyRepository companyRepository, BackPackRepository backPackRepository, StockRepository stockRepository, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.backPackRepository = backPackRepository;
        this.stockRepository = stockRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseEntity<?> buy(HttpServletRequest httpServletRequest, BuyBody buyBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        CompanyEntity companyEntity = companyRepository.findById(buyBody.getId()).orElse(null);
        if (companyEntity == null){
            return ResponseEntity.notFound().build();
        }

        List<StockEntity> stockEntityList = stockRepository.findByCompanyId(buyBody.getId());

        StockUtils stockUtils = new StockUtils();
        StockDto stockDto = stockUtils.getStock(stockEntityList);
        if (userEntity.getBalance().compareTo(stockDto.getCost()) < 0){
            return ResponseEntity.badRequest().body("Not enough funds!");
        }

        if (companyEntity.getStock() < buyBody.getCount()){
            return ResponseEntity.badRequest().body("Promotions are over!");
        }

        userEntity.setBalance(userEntity.getBalance().subtract(stockDto.getCost()));
        companyEntity.setStock(companyEntity.getStock() - buyBody.getCount());

        BackPackEntity backPackEntity = backPackRepository.findByUserIdAndCompanyId(userId, companyEntity.getId()).orElse(null);
        if (backPackEntity == null){
            backPackEntity = new BackPackEntity();
            backPackEntity.setCompanyId(companyEntity.getId());
            backPackEntity.setUserId(userId);
        }

        backPackEntity.setCount(backPackEntity.getCount() + buyBody.getCount());

        userRepository.save(userEntity);
        companyRepository.save(companyEntity);
        backPackRepository.save(backPackEntity);

        return ResponseEntity.ok("You have purchased shares!");
    }


    public ResponseEntity<?> sell(HttpServletRequest httpServletRequest, SellBody sellBody){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        BackPackEntity backPackEntity = backPackRepository.findByUserIdAndCompanyId(userId, sellBody.getId()).orElse(null);
        if (backPackEntity == null){
            return ResponseEntity.notFound().build();
        }

        if (backPackEntity.getCount() < sellBody.getCount()){
            return ResponseEntity.badRequest().body("You don't have that many shares!");
        }

        CompanyEntity companyEntity = companyRepository.findById(sellBody.getId()).orElse(null);
        if (companyEntity == null){
            return ResponseEntity.notFound().build();
        }

        List<StockEntity> stockEntityList = stockRepository.findByCompanyId(sellBody.getId());

        StockUtils stockUtils = new StockUtils();
        StockDto stockDto = stockUtils.getStock(stockEntityList);

        userEntity.setBalance(userEntity.getBalance().subtract(stockDto.getCost().multiply(BigDecimal.valueOf(sellBody.getCount()))));
        backPackEntity.setCount(backPackEntity.getCount() - sellBody.getCount());
        companyEntity.setStock(companyEntity.getStock() + sellBody.getCount());

        userRepository.save(userEntity);
        backPackRepository.save(backPackEntity);
        companyRepository.save(companyEntity);

        return ResponseEntity.ok("You have purchased shares!");
    }
}
