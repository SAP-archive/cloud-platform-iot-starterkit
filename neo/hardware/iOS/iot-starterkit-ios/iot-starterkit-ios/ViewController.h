//
//  ViewController.h
//  iot-starterkit-ios
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController <NSURLSessionDelegate, UITextFieldDelegate>

//IBOutlets
@property (strong, nonatomic) IBOutlet UITextField *tfAccountID;
@property (strong, nonatomic) IBOutlet UITextField *tfDataCenter;

@property (strong, nonatomic) IBOutlet UITextField *tfDeviceName;

@property (strong, nonatomic) IBOutlet UITextField *tfDeviceTypeID;
@property (strong, nonatomic) IBOutlet UITextField *tfDeviceRegistrationToken;

@property (strong, nonatomic) IBOutlet UITextField *tfMessageTypeID;

@property (strong, nonatomic) IBOutlet UITextField *tfDeviceID;
@property (strong, nonatomic) NSString *deviceToken;

@property (strong, nonatomic) IBOutlet UISlider *slValue;

//NSURLSession
@property (strong, nonatomic) NSURLSession *session;
@property (strong, nonatomic) NSMutableDictionary *responsesData;

@end

