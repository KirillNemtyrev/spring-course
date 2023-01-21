package com.community.server.service;

import com.community.server.dto.CompanyDto;
import com.community.server.dto.ProfileDto;
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
import java.util.*;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StockRepository stockRepository;
    private final BackPackRepository backPackRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;

    public ProfileService(UserRepository userRepository, CompanyRepository companyRepository, StockRepository stockRepository, BackPackRepository backPackRepository, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.stockRepository = stockRepository;
        this.backPackRepository = backPackRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ProfileDto getProfile(HttpServletRequest httpServletRequest){

        String jwt = jwtAuthenticationFilter.getJwtFromRequest(httpServletRequest);
        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        ProfileDto profileDto = new ProfileDto();
        profileDto.setUsername(userEntity.getUsername());
        profileDto.setName(userEntity.getName());
        profileDto.setBalance(userEntity.getBalance());

        Map<Long, CompanyDto> helper = new HashMap<>();

        List<CompanyEntity> company = companyRepository.findAll();
        List<BackPackEntity> backPackEntities = backPackRepository.findByUserId(userId);
        List<CompanyDto> companyList = new ArrayList<>();

        for (CompanyEntity companyEntity : company) {

            if (companyEntity.getStock() == 0){
                continue;
            }

            List<StockEntity> stocks = stockRepository.findByCompanyId(companyEntity.getId());
            stocks.sort(Comparator.comparing(StockEntity::getDate));

            if (stocks.size() == 0) {
                continue;
            }

            StockUtils stockUtils = new StockUtils();
            StockDto stockDto = stockUtils.getStock(stocks);

            if (stockDto == null){
                continue;
            }

            CompanyDto companyDto = new CompanyDto();
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
            helper.put(companyEntity.getId(), companyDto);
        }
        companyList.sort(Comparator.comparing(CompanyDto::getCost));
        profileDto.setCompany(companyList);

        for (BackPackEntity backPackEntity : backPackEntities) {

            CompanyDto companyDto = helper.get(backPackEntity.getCompanyId());
            if (companyDto == null) {
                continue;
            }

            profileDto.setBackpack(profileDto.getBackpack().add(companyDto.getCost().multiply(BigDecimal.valueOf(backPackEntity.getCount()))));
        }

        return ResponseEntity.ok(profileDto).getBody();

    }
}
