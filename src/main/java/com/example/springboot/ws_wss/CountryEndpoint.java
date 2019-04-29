package com.example.springboot.ws_wss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.springboot.ws_wss.common.Constant;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

@Endpoint
public class CountryEndpoint {
	public static final String ENDPOINT_NAME = "countries";
	public static final String LOCATION_URI = Constant.GLOBAL_URI + "/"+ ENDPOINT_NAME;
	public static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
	public static final String COUNTRIES_PORT = "CountriesPort";
	public static final String COUNTRIES_SCHEMA = ENDPOINT_NAME + ".xsd";

	private CountryRepository countryRepository;

	@Autowired
	public CountryEndpoint(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
	@ResponsePayload
	public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
		GetCountryResponse response = new GetCountryResponse();
		response.setCountry(countryRepository.findCountry(request.getName()));

		return response;
	}
}
