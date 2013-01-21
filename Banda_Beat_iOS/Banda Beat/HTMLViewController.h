//
//  AboutViewController.h
//  Banda Beat
//
//  Created by iker on 20/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAI.h"

@interface HTMLViewController : GAITrackedViewController
@property(strong, nonatomic) NSString *fileName;
@property(weak, nonatomic) IBOutlet UIWebView *web;
-(IBAction)cancel:(id)sender;
@end
