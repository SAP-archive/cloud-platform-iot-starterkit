//
//  ViewController.m
//  iot-starterkit-ios
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.responsesData = [[NSMutableDictionary alloc] init];
    
    //Initialize text fields
    self.tfAccountID.placeholder = @"Account ID";
    self.tfAccountID.text = @"";
    
    self.tfDataCenter.placeholder = @"Data Center (hanatrial / hana.eu1 / ...)";
    self.tfDataCenter.text = @"";
    
    self.tfDeviceName.text = @"Device Name";
    self.tfDeviceName.text = @"";
    
    self.tfDeviceTypeID.placeholder = @"not registered";
    self.tfDeviceTypeID.text = @"";
    
    self.tfDeviceRegistrationToken.placeholder = @"not registered";
    self.tfDeviceRegistrationToken.text = @"";
    
    self.slValue.minimumValue = 0;
    self.slValue.maximumValue = 100;
    self.slValue.continuous = NO;
    self.slValue.value = 50;
    
    //Optional to initialize the account data
    [self setDefaultData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

# pragma Initialization

- (void) setDefaultData {
    
    //set delegate to dismiss keyboard
    self.tfAccountID.delegate = self;
    self.tfDataCenter.delegate = self;
    self.tfDeviceName.delegate = self;
    self.tfDeviceTypeID.delegate = self;
    self.tfDeviceRegistrationToken.delegate = self;
    self.tfMessageTypeID.delegate = self;
    
    self.tfAccountID.text = @""; //e.g.: p123456789trial
    self.tfDataCenter.text = @""; //e.g., hanatrial, hana.eu1, hana.us1, hana.us2
    self.tfDeviceName.text = @""; //any name
    
    self.tfDeviceTypeID.text = @"";
    self.tfDeviceRegistrationToken.text = @"";
    
    self.tfMessageTypeID.text = @"";
}

# pragma IoT Services RDMS

- (void) registerDevice {
    
    NSURL* url = [self getRDMSURL:@"deviceregistration" forDataCenter:self.tfDataCenter.text forAccountID:self.tfAccountID.text];

    NSString *jsonRequest = [NSString stringWithFormat:@"{\"name\":\"%@\", \"device_type\":\"%@\"}", self.tfDeviceName.text, self.tfDeviceTypeID.text];
    NSLog(@"RDMS Registration Request: %@", jsonRequest);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    
    NSData *requestData = [NSData dataWithBytes:[jsonRequest UTF8String] length:[jsonRequest length]];
    [request setHTTPMethod:@"POST"];
    
    NSLog(@"MMS Message Token: %@", self.tfDeviceRegistrationToken.text);
    
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setValue:[NSString stringWithFormat:@"Bearer %@", self.tfDeviceRegistrationToken.text] forHTTPHeaderField:@"Authorization"];
    [request setHTTPBody: requestData];

    self.session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
                                                          delegate:self
                                                     delegateQueue:[NSOperationQueue mainQueue]];

    NSURLSessionDataTask *sessionTask = [self.session dataTaskWithRequest:request];
    [sessionTask resume];
}

- (NSURL*) getRDMSURL:(NSString*)apiName forDataCenter:(NSString*)dataCenter forAccountID:(NSString*)accountID{
    
    NSString* urlString = [NSString stringWithFormat:@"https://iotrdmsiotservices-%@.%@.ondemand.com/com.sap.iotservices.dms/v1/api/%@", accountID, dataCenter, apiName];
    NSLog(@"RDMS URL: %@", urlString);
    return [NSURL URLWithString:urlString];
}

# pragma IoT Services MMS

- (void) sendMessage {
    
    long timestamp = [[NSDate date] timeIntervalSince1970];
        
    NSString *jsonRequest = @"";
    //the JSON request needs to be adjust according to the defined message format, here the message type for the iot starter kit is used
    jsonRequest = [NSString stringWithFormat:@"{\"mode\":\"async\", \"messageType\":\"%@\", \"messages\":[{\"timestamp\":\"%ld\", \"sensor\":\"%@\", \"value\":\"%f\"}]}", self.tfMessageTypeID.text, timestamp, @"ios_app", self.slValue.value];
    
    NSLog(@"MMS Message Request: %@", jsonRequest);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[[self getMMSURL] URLByAppendingPathComponent:self.tfDeviceID.text]];
    NSData *requestData = [NSData dataWithBytes:[jsonRequest UTF8String] length:[jsonRequest length]];
    
    [request setHTTPMethod:@"POST"];
    
    [request setValue:[NSString stringWithFormat:@"Bearer %@", self.deviceToken] forHTTPHeaderField:@"Authorization"];
    [request setValue:@"application/json;charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    [request setValue:[NSString stringWithFormat:@"%lu", (unsigned long)[requestData length]] forHTTPHeaderField:@"Content-Length"];
    [request setHTTPBody: requestData];
    
    self.session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
                                                 delegate:self
                                            delegateQueue:[NSOperationQueue mainQueue]];
    
    
    NSURLSessionDataTask *sessionTask = [self.session dataTaskWithRequest:request];
    [sessionTask resume];
}

- (NSURL*) getMMSURL {
    NSString* value = [NSString stringWithFormat:@"https://iotmms%@.%@.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/", self.tfAccountID.text, self.tfDataCenter.text];
    return [NSURL URLWithString:value];
}

# pragma NSURLSession Delegate

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    
    if (error) {
        NSLog(@"%@ failed: %@", task.originalRequest.URL, error);
    }
    
    NSMutableData *responseData = self.responsesData[@(task.taskIdentifier)];
    
    if (responseData) {
        // my response is JSON; I don't know what yours is, though this handles both
        
        NSDictionary *response = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
        if (response) {
            NSLog(@"Response (JSON): %@", response);
            
            // handle RDMS registration response
            if ([response objectForKey:@"device_type"] && [response objectForKey:@"id"] && [response objectForKey:@"token"] && [response objectForKey:@"name"]) {
                self.tfDeviceID.text = [response objectForKey:@"id"];
                self.deviceToken = [response objectForKey:@"token"];
            }
            // Handle MMS messaging response
            else if ([response objectForKey:@"msg"]){
                // handle message management response
                NSLog(@"MMS Message received %@", [response objectForKey:@"msg"]);
            }
            
            // handle other responses

        } else {
            NSLog(@"Response (Data): %@", [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding]);
        }
        
        [self.responsesData removeObjectForKey:@(task.taskIdentifier)];
    } else {
        NSLog(@"No response data received.");
    }
    
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    
    NSMutableData *responseData = self.responsesData[@(dataTask.taskIdentifier)];
    if (!responseData) {
        responseData = [NSMutableData dataWithData:data];
        self.responsesData[@(dataTask.taskIdentifier)] = responseData;
    } else {
        [responseData appendData:data];
    }
}

# pragma UIButton Actions

- (IBAction)sendMessageClicked:(id)sender {
    [self sendMessage];
}

- (IBAction)registerDeviceClicked:(id)sender {
    [self registerDevice];
}

#pragma UITextField Delegate

// dissmiss keyboard on return
- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return YES;
}

@end
