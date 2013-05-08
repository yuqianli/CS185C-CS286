//
//  SJSUViewController.m
//  ShareDemo
//
//  Created by David Li on 3/25/13.
//  Copyright (c) 2013 Xiaohan Li. All rights reserved.
//

#import "SJSUViewController.h"

@interface SJSUViewController ()

@end

@implementation SJSUViewController

@synthesize share_image, share_text;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)share_btn_touch:(id)sender {
    NSArray *activityItems = [NSArray arrayWithObjects: share_text.text, share_image.image , nil];
    
    UIActivityViewController *activityController = [[UIActivityViewController alloc] initWithActivityItems:activityItems applicationActivities:nil];
    [self presentViewController:activityController animated:YES completion:nil];
}

@end
