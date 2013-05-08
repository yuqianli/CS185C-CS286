//
//  SJSUViewController.h
//  ShareDemo
//
//  Created by David Li on 3/25/13.
//  Copyright (c) 2013 Xiaohan Li. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SJSUViewController : UIViewController
@property (nonatomic, retain) IBOutlet UILabel *share_text;
@property (nonatomic, retain) IBOutlet UIImageView *share_image;

-(IBAction)share_btn_touch:(id)sender;

@end
